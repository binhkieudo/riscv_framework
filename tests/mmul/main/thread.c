#include "thread.h"
#include "asm.h"

void core_enable(unsigned int hartid) {
    if (hartid == 1) core1_en = 1;
    else if (hartid == 2) core2_en = 1;
    else if (hartid == 3) core3_en = 1;
}

void core_disable(unsigned int hartid) {
    if (hartid == 1) core1_en = 0;
    else if (hartid == 2) core2_en = 0;
    else if (hartid == 3) core3_en = 0;
}

void thread_init(void) {
    mux_lock();
    *threadWrPtr = (struct threadPointer *)THREAD_QUEUE;
    *threadRdPtr = (struct threadPointer *)THREAD_QUEUE;
    threadWrPtr_val = THREAD_QUEUE;
    threadId = 0;
    mux_unlock();
    
    // REG32(clint, CLINT_MSIP1) = CLINT_MSIPEN;
    // REG32(clint, CLINT_MSIP2) = CLINT_MSIPEN;
    // REG32(clint, CLINT_MSIP3) = CLINT_MSIPEN;
}

void thread_create(void *func_ptr) {
    (*threadWrPtr)->address = func_ptr;
    (*threadWrPtr)->thread_id = threadId;

    threadWrPtr_val = threadWrPtr_val + threadPointerSize;
    *threadWrPtr = (volatile struct threadPointer*)threadWrPtr_val;
    threadId    = threadId + 1;
}

void thread_join(void) {
    (*threadWrPtr)->address = 0;
    (*threadWrPtr)->thread_id = 0xffffffff;

    mux_lock();
    if ((*threadRdPtr)->thread_id != 0xffffffff) {
        mux_unlock();
        void *hart0_ptr = (void*)HART0_START;
        void (*func_ptr)(void) = hart0_ptr;
        (*func_ptr)();
    }
    else mux_unlock();
}