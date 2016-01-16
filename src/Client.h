#ifndef SRC_CLIENT_H_
#define SRC_CLIENT_H_


class Client {
public:
    static double m_targetPosition;
    static char m_receivedData[];
    static bool receive;

    Client();

    void receivePacket(void*);

    void sendPacket();

    virtual ~Client();

};

#endif
