"""
Local preview server for a0 design studio.
Uses Python standard library so it runs anywhere without dependencies.
"""
from __future__ import annotations

import http.server
import json
import os
import socketserver
import urllib.request
from pathlib import Path

PORT = 8080
BASE_DIR = Path(__file__).resolve().parent

class StudioHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(BASE_DIR), **kwargs)

    def do_GET(self):
        if self.path in ("/", "/studio", "/studio.html", "/index.html"):
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            studio_file = BASE_DIR / "studio.html"
            if studio_file.exists():
                self.wfile.write(studio_file.read_bytes())
            else:
                self.wfile.write(b"<h1>a0 Design Studio</h1>")
            return
        super().do_GET()

if __name__ == "__main__":
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), StudioHandler) as httpd:
        print(f"Serving a0 design studio at http://localhost:{PORT}")
        httpd.serve_forever()
