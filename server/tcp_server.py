import asyncio
import threading


class ClientGroup:
    def __init__(self) -> None:
        self.clients = []

    def add_client(self, client):
        self.clients.append(client)

    def remove_client(self, client):
        self.clients.remove(client)

    def sendall(self, msg):
        for client in self.clients:
            client.send_msg(msg)


class HttpProtocol(asyncio.Protocol):
    def __init__(self, client_group):
        self.transport = None
        self.client_group = client_group
        self.client_group.add_client(self)

    def connection_made(self, transport):
        print("client connecct : ", transport.get_extra_info('peername'))
        self.transport = transport

    def connection_lost(self, exc):
        print("client disconnecct : ", self.transport.get_extra_info('peername'))
        self.client_group.remove_client(self)

    def send_msg(self, msg):
        self.transport.write(msg.encode())


def msg_notify(cg, q):
    while True:
        item = q.get()
        if item:
          cg.sendall(f'{item["title"]}{chr(1)}{item["content"]}')


def run_noify_server(host, port, q):
    async def run():
        loop = asyncio.get_event_loop()
     
        cg = ClientGroup()
        thread = threading.Thread(target=msg_notify, args=(cg, q))
        thread.start()
     
        s = await loop.create_server(lambda: HttpProtocol(cg), host=host, port=port)
        async with s:
            print("Start listening on ", port)
            await s.serve_forever()

    asyncio.run(run())