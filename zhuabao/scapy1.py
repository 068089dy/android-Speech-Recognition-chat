from scapy.all import *

pcap = sniff(iface = "enp2s0", count = 50)
print pcap[1].show()
