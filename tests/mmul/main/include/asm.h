#ifndef _ASM_H
#define _ASM_H

#include "platform.h"

extern char _heap_share[];

extern void mux_lock();
extern void mux_unlock();
extern void sem_wait(char* channel);
extern void sem_signal(char* channel);
extern unsigned int get_hartid(void);
extern void atomic_write(char* addr, unsigned int value);

extern unsigned int randi(unsigned int limit);

#endif