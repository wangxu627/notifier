import asyncio
import time
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


# 创建http协议处理对象
class HttpProtocol(asyncio.Protocol):
    def __init__(self, client_group):
        self.transport = None
        self.client_group = client_group
        self.client_group.add_client(self)

    def connection_made(self, transport):
        self.transport = transport       # 将上下文对象保存到对象属性中

    def connection_lost(self, exc):
        self.client_group.remove_client(self)

    def send_msg(self, msg):
        self.transport.write(msg.encode())


def msg_notify(cg):
    print("This is another function running in a separate thread.", cg)
    while True:
        print("===========>>> : ", cg)
        time.sleep(10)
        cg.sendall("hello : " + str(time.ctime()))


async def run():
    host, port = "0.0.0.0", 8991
    loop = asyncio.get_event_loop()
    cg = ClientGroup()

    thread = threading.Thread(target=msg_notify, args=(cg, ))
    thread.start()

    s = await loop.create_server(lambda: HttpProtocol(cg), host=host, port=port)
    async with s:
        print("Start listening on 8991")
        # 开启服务器事件循环
        await s.serve_forever()

asyncio.run(run())