#ifndef _THREAD_H
#define _THREAD_H

#include <stdint.h>
#include "platform.h"
#include "include/devices/clint.h"

#define THREAD_QUEUE BRAM_MEM_ADDR

#define HART0_START _AC(0x80000038,UL)

#define REG32(p, i)	((p)[(i) >> 2])

static volatile uint32_t * const clint = (void *)(CLINT_CTRL_ADDR);

struct threadPointer {
	void *address;
	unsigned int thread_id;
};

static const unsigned int threadPointerSize = sizeof(struct threadPointer);

static volatile struct threadPointer** threadRdPtr = (volatile struct threadPointer**)THREAD_RDPTR;
static volatile struct threadPointer** threadWrPtr = (volatile struct threadPointer**)THREAD_WRPTR;

static volatile unsigned long threadWrPtr_val;
static volatile unsigned int threadId;

static volatile unsigned int core1_en = 0;
static volatile unsigned int core2_en = 0;
static volatile unsigned int core3_en = 0;

void thread_init(void);
void thread_create(void *, unsigned int);
void thread_join(void);


#endif