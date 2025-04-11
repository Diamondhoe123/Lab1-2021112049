#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <dirent.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/stat.h>

#define BUF_SIZE 1024
#define PORT 8888

void handle_client(int sock);

int main() {
    int serv_sock, clnt_sock;
    struct sockaddr_in serv_addr, clnt_addr;
    socklen_t addr_size;

    serv_sock = socket(AF_INET, SOCK_STREAM, 0);
    memset(&serv_addr, 0, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    serv_addr.sin_port = htons(PORT);

    bind(serv_sock, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    listen(serv_sock, 5);

    while(1) {
        addr_size = sizeof(clnt_addr);
        clnt_sock = accept(serv_sock, (struct sockaddr*)&clnt_addr, &addr_size);
        printf("已连接客户端\n");
        handle_client(clnt_sock);
        close(clnt_sock);
    }
    close(serv_sock);
    return 0;
}

void handle_client(int sock) {
    char op;
    char buf[BUF_SIZE];
    char filename[256];
    FILE *fp;
    size_t filesize, received;
    struct dirent ​**filelist;
    int n;

    read(sock, &op, 1);

    if(op == 's') {
        // 接收文件
        read(sock, filename, 256);
        read(sock, &filesize, sizeof(filesize));
        filesize = ntohl(filesize);
        
        printf("正在接收文件: %s (%lu bytes)\n", filename, filesize);
        
        fp = fopen(filename, "wb");
        received = 0;
        while(received < filesize) {
            int len = read(sock, buf, BUF_SIZE);
            fwrite(buf, 1, len, fp);
            received += len;
        }
        fclose(fp);
        printf("文件接收成功\n");
    }
    else if(op == 'd') {
        // 生成文件列表
        n = scandir(".", &filelist, NULL, alphasort);
        char listbuf[4096] = {0};
        for(int i=0; i<n; i++) {
            if(filelist[i]->d_type == DT_REG) {
                strcat(listbuf, filelist[i]->d_name);
                strcat(listbuf, "\n");
            }
            free(filelist[i]);
        }
        free(filelist);
        write(sock, listbuf, 4096);

        // 接收请求的文件名
        read(sock, filename, 256);
        struct stat st;
        if(stat(filename, &st) != 0) {
            filesize = 0;
            filesize = htonl(filesize);
            write(sock, &filesize, sizeof(filesize));
            return;
        }
        filesize = st.st_size;
        filesize = htonl(filesize);
        write(sock, &filesize, sizeof(filesize));
        filesize = ntohl(filesize);

        // 发送文件内容
        fp = fopen(filename, "rb");
        while((received = fread(buf, 1, BUF_SIZE, fp)) > 0) {
            write(sock, buf, received);
        }
        fclose(fp);
        printf("文件发送成功\n");
    }
}