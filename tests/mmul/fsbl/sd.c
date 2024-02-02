// See LICENSE.Sifive for license details.
#include <stdint.h>
#include "include/devices/clint.h"
#include <platform.h>

#include "common.h"
#include "smp.h"
#include "asm.h"

#define DEBUG
#include "kprintf.h"
#include "asm.h"

#define THREAD_QUEUE BRAM_MEM_ADDR

struct threadPointer {
	void *address;
	unsigned int thread_id;
};

static volatile struct threadPointer** threadRdPtr = (volatile struct threadPointer**)THREAD_RDPTR;
static volatile struct threadPointer** threadWrPtr = (volatile struct threadPointer**)THREAD_WRPTR;
static const unsigned int threadPointerSize = sizeof(struct threadPointer);



int main(int mhartid, char** dump)
{
	// *threadRdPtr = (struct threadPointer *)THREAD_QUEUE;

	volatile unsigned long threadRdPtr_val;
	volatile unsigned long address;
	volatile unsigned int thread_id;

	unsigned long threadWrPtr_val;

	do {
		// Get thread info
		mux_lock();
		address = (unsigned long)(*threadRdPtr)->address;
		threadRdPtr_val = *threadRdPtr;
		threadWrPtr_val = *threadWrPtr;
		thread_id = (unsigned int)(*threadRdPtr)->thread_id;
		if (threadRdPtr_val < threadWrPtr_val) {
			threadRdPtr_val = threadRdPtr_val + threadPointerSize;
			*threadRdPtr = (volatile struct threadPointer*)threadRdPtr_val;
			mux_unlock();
			// Run thread
			void (*func_ptr)(int, unsigned int) = (void*)address;
			(*func_ptr)(mhartid, thread_id);
			// mux_unlock();
		}
		else if (threadRdPtr_val == threadWrPtr_val) {
			if (thread_id == 0xffffffff) { // ternimate signal
				mux_unlock();
				break;
			}
			else { // perform then wait for next thread
				threadRdPtr_val = threadRdPtr_val + threadPointerSize;
				*threadRdPtr = (volatile struct threadPointer*)threadRdPtr_val;
				mux_unlock();
				void (*func_ptr)(int, unsigned int) = (void*)address;
				(*func_ptr)(mhartid, thread_id);
				// mux_unlock();
			}
		}
		mux_unlock();
		// do nothing otherwise
	}
	while (1);
	
	return 0;
}
