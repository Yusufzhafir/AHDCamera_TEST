#include "gpio.h"

#define LOG_TAG "gpio_zyz"

#ifndef EXEC
#include <android/log.h>
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...) printf(">==< %s >==< ",LOG_TAG),printf(__VA_ARGS__),printf("\n")
#endif

int read_gpio(char *path, void (*callback)(int)){
    int fd = open(path, O_RDONLY);
    char buf[11];
    int res = 0;

    if(fd == -1){
        perror("error opening file");
        return -1;
    }

    struct pollfd gpio_poll_fd = {
        .fd = fd,
        .events = POLLPRI,
        .revents = 0
    };

    for(;;){
        res = poll(&gpio_poll_fd,1,-1);
        if(res == -1){
            perror("error polling");
            return -2;
//            break;
        }

        if((gpio_poll_fd.revents & POLLPRI)  == POLLPRI){
            LOGD("POLLPRI");
            int off = lseek(fd, 0, SEEK_SET);
            if(off == -1) return -2;//break;
            memset(buf, 0, 11);
            size_t num = read(fd, buf, 10*sizeof(char));
            callback(atoi(buf));
        }

        if((gpio_poll_fd.revents & POLLERR) == POLLERR){
            //seems always to be true ..
            //LOGD("POLLERR");
        }
//        sleep(1);
    }
    return 0;
}
