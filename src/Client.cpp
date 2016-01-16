#include "Client.h"
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#define BUFLEN 1024
#define NPACK 1
#define PORT 31415
#define SRV_IP "999.999.999.999" //change ip address



Client::Client() {
    bool receive = true;

    pthread_t receiveThread;

    pthread_create(&receiveThread, NULL, &this->receivePacket(NULL),NULL);

    char m_receivedData[1024];

}

void Client::receivePacket(void *){

    struct sockaddr_in si_me, si_other;
        int receiveSocket, slen=sizeof(si_other);
        char buf[BUFLEN];

        receiveSocket=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

        memset((char *) &si_me, 0, sizeof(si_me));

        si_me.sin_family = AF_INET;

        si_me.sin_port = htons(PORT);

        si_me.sin_addr.s_addr = htonl(INADDR_ANY);

        bind(receiveSocket, &si_me, sizeof(si_me));

        while (receive){
            recvfrom(receiveSocket,buf,BUFLEN,0, &si_other, &slen);
            m_receivedData=buf;
        }
}

void Client::sendPacket() {

}

Client::~Client() {
}
