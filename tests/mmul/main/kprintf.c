// See LICENSE.Sifive for license details.
#include <stdarg.h>
#include <stdint.h>
#include <stdbool.h>

#include "kprintf.h"

static inline void _kputs(const char *s)
{
	char c;
	for (; (c = *s) != '\0'; s++)
		kputc(c);
}

void kputs(const char *s)
{
	_kputs(s);
	kputc('\r');
	kputc('\n');
}

void kprintf(const char *fmt, ...)
{
	va_list vl;
	bool is_format, is_long, is_char;
	char c;

	va_start(vl, fmt);
	is_format = false;
	is_long = false;
	is_char = false;
	while ((c = *fmt++) != '\0') {
		if (is_format) {
			switch (c) {
			case 'l':
				is_long = true;
				continue;
			case 'h':
				is_char = true;
				continue;
			case 'x': {
				unsigned long n;
				long i;
				if (is_long) {
					n = va_arg(vl, unsigned long);
					i = (sizeof(unsigned long) << 3) - 4;
				} else {
					n = va_arg(vl, unsigned int);
					i = is_char ? 4 : (sizeof(unsigned int) << 3) - 4;
				}
				char is_print = 0;
				for (; i >= 0; i -= 4) {
					long d;
					d = (n >> i) & 0xF;
					char c_temp = d < 10 ? '0' + d : 'a' + d - 10;
					if (c_temp == '0' && (is_print == 0)) continue;
					is_print = 1;
					kputc(c_temp);
				}
				if (is_print == 0) kputc('0');
				break;
			}
			case 'd':
				unsigned int n  = va_arg(vl, unsigned int);
				int i = 0;
				for (i = 0; n > 0; i++) {
					unsigned int d = n % 10;
					char c_temp = (d & 0xff) + 0x30;
					kputc(c_temp);
					n = n / 10;
				}
				if (i == 0) kputc('0');
				break;
			case 's':
				_kputs(va_arg(vl, const char *));
				break;
			case 'c':
				kputc(va_arg(vl, int));
				break;
			}
			is_format = false;
			is_long = false;
			is_char = false;
		} else if (c == '%') {
			is_format = true;
		} else {
			kputc(c);
		}
	}
	va_end(vl);
}
