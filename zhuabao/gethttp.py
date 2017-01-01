#! /usr/bin/python2
# coding:utf-8

from scapy.all import *
import re

def get_http_header(http_payload):
    try:
        header_raw = http_payload[:http_payload.index("\r\n\r\n")+2]
        headers = dict(re.findall(r"(?P<name>.*?):(?P<value>.*?)\r\n"),header_raw)
    except:
        return None

    if "Content-Type" not in headers:
        return None
    print headers
    return headers


def get_cookie(http_payload):
    try:
        cookies = re.findall(r"(?m)Cookie: myusername=.*$",http_payload)
    except:
        return None
    return cookies

pcap_file = "demo.pacp"

a = rdpcap(pcap_file)

sessions = a.sessions()

for session in sessions:
    http_payload = ""
    for packet in sessions[session]:
        try:
            if packet[TCP].dport == 80 or packet[TCP].sport == 80:
                http_payload += str(packet[TCP].payload)
                http_payload = unicode(http_payload,'GBK').encode('UTF-8')
                #print http_payload
        except:
            pass

        cookies = get_cookie(http_payload)

        if cookies is None:
            continue
        for cookie in cookies:
            print cookie
