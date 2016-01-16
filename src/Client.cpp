#include "Client.h"
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#define BUFLEN 1024
#define NPACK 1
#define PORT 1234
#define SRV_IP "999.999.999.999" //change ip address
/* diep(), #includes and #defines like in the server */


Client::Client {
	struct sockaddr_in si_me, si_other;
	 int s, i, slen=sizeof(si_other);
	 char buf[BUFLEN];
	 s=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	 memset((char *) &si_other, 0, sizeof(si_other));
	 si_other.sin_family = AF_INET;
	 si_other.sin_port = htons(PORT);
	 inet_aton(SRV_IP, &si_other.sin_addr);
}

void Client::sendPacket() {
	sendto(s, buf, BUFLEN, 0, &si_other, slen);
	recvfrom(s, buf, BUFLEN, 0, &si_other, &slen);
}

Client::~Client() {
}
