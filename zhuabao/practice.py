#! /usr/bin/python2
# coding:utf-8

from scapy.all import *
from time import ctime,sleep
import threading
TIMEOUT = 4
conf.verb=0


def pro(cc):

    dst = "172.18.208." + str(cc)
    packet = IP(dst=dst, ttl=20)/ICMP()
    reply = sr1(packet, timeout=TIMEOUT)
    if not (reply is None):
        #handle.write(reply.src+" is online"+"\n")
        print reply.src, "is online"

for i in range(2,254):
    pro(i)
