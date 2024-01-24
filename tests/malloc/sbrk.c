#include <stddef.h>
#include <errno.h>
#include "syscall.h"
#include "compiler.h"

extern char __heap_start[];
static char *curbrk = __heap_start;

void *_sbrk(ptrdiff_t incr)
{
    extern char __heap_end[];
    char *newbrk;
    char *oldbrk;

    oldbrk = curbrk;
    newbrk = oldbrk + incr;
    if (unlikely((newbrk < __heap_start) || (newbrk >= __heap_end))) {
        errno = ENOMEM;
        return (void *)(-1);
    }

    curbrk = newbrk;
    return oldbrk;
}
