# HandiServe

Multi-user telepresence robot arm controller server using websockets.

# Notes

  Add to Eclipse VM-Arguments: 
    `-Djava.library.path=target/natives`

  Reverse tunneling:
    `ssh -g -R 0.0.0.0:14444:localhost:14444 <server>`
