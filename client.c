// 客户端 client.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/stat.h>

#define BUF_SIZE 1024
#define PORT 8888

void send_file(int sock, char *filename);
void download_file(int sock);

int main() {
    int sock;
    struct sockaddr_in serv_addr;
    char choice[10];

    while(1) {
        printf("\n1. 发送文件\n2. 下载文件\n3. 退出\n选择: ");
        fgets(choice, 10, stdin);
        
        if(strcmp(choice, "1\n") == 0) {
            sock = socket(AF_INET, SOCK_STREAM, 0);
            memset(&serv_addr, 0, sizeof(serv_addr));
            serv_addr.sin_family = AF_INET;
            serv_addr.sin_addr.s_addr = inet_addr("SERVER_IP"); // 替换服务端IP
            serv_addr.sin_port = htons(PORT);
            
            if(connect(sock, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) == -1) {
                perror("Connect failed");
                continue;
            }
            
            send_file(sock);
            close(sock);
        }
        else if(strcmp(choice, "2\n") == 0) {
            sock = socket(AF_INET, SOCK_STREAM, 0);
            memset(&serv_addr, 0, sizeof(serv_addr));
            serv_addr.sin_family = AF_INET;
            serv_addr.sin_addr.s_addr = inet_addr("SERVER_IP"); // 替换服务端IP
            serv_addr.sin_port = htons(PORT);
            
            if(connect(sock, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) == -1) {
                perror("Connect failed");
                continue;
            }
            
            download_file(sock);
            close(sock);
        }
        else if(strcmp(choice, "3\n") == 0) {
            break;
        }
    }
    return 0;
}

void send_file(int sock) {
    char filename[256];
    char buf[BUF_SIZE];
    FILE *fp;
    size_t filesize, sent;
    struct stat st;

    printf("输入你想发送的文件: ");
    fgets(filename, 256, stdin);
    filename[strcspn(filename, "\n")] = 0;

    if(stat(filename, &st) != 0) {
        printf("File not found\n");
        return;
    }
    filesize = st.st_size;

    // 发送操作类型
    write(sock, "s", 1);
    
    // 发送文件名和大小
    write(sock, filename, 256);
    filesize = htonl(filesize);
    write(sock, &filesize, sizeof(filesize));
    filesize = ntohl(filesize);

    fp = fopen(filename, "rb");
    sent = 0;
    while(sent < filesize) {
        int len = fread(buf, 1, BUF_SIZE, fp);
        write(sock, buf, len);
        sent += len;
    }
    fclose(fp);
    printf("成功发送文件\n");
}

void download_file(int sock) {
    char buf[BUF_SIZE];
    char filelist[4096];
    char filename[256];
    FILE *fp;
    size_t filesize, received;

    // 发送操作类型
    write(sock, "d", 1);
    
    // 接收文件列表
    read(sock, filelist, 4096);
    printf("可发送的文件:\n%s\n", filelist);

    printf("输入需要下载的文件名: ");
    fgets(filename, 256, stdin);
    filename[strcspn(filename, "\n")] = 0;
    write(sock, filename, 256);

    // 接收文件大小
    read(sock, &filesize, sizeof(filesize));
    filesize = ntohl(filesize);
    if(filesize == 0) {
        printf("文件不在服务器上\n");
        return;
    }

    fp = fopen(filename, "wb");
    received = 0;
    while(received < filesize) {
        int len = read(sock, buf, BUF_SIZE);
        fwrite(buf, 1, len, fp);
        received += len;
    }
    fclose(fp);
    printf("文件成功下载\n");
}