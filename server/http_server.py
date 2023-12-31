import threading
import queue
from fastapi import FastAPI
from pydantic import BaseModel
from tcp_server import run_noify_server

HTTP_PORT = 8990
TCP_PORT = 8991
HOST = '0.0.0.0'

app = FastAPI()
app.q = queue.Queue()

class MsgItem(BaseModel):
    msg: str


@app.get("/msg/")
def post_msg(title: str, content: str):
    app.q.put({
        "title": title or '',
        "content": content or ''
    })
    return {"message": "queued"}

def main():
    thread = threading.Thread(target=run_noify_server, args=(HOST, TCP_PORT, app.q))
    thread.start()

    import uvicorn
    uvicorn.run(app, host=HOST, port=HTTP_PORT)

if __name__ == "__main__":
    main()
