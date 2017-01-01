# coding;utf-8
import re

fp = open("demo.pacp","r")
pacp = fp.read()
cookies = re.findall(r"Cookie: myusername=.*",pacp)
cookie1 = None
for cookie in cookies:
    if cookie1 == None:
        print cookie
    elif cookie1 !=cookie:
        print cookie
    cookie1 = cookie
