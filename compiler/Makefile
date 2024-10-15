CC = gcc
all: CFLAGS = -Wall -Wextra -Iinclude -Isrc
debug: CFLAGS = -Wall -Wextra -Iinclude -Isrc -g -O0
SRCDIR = src
BUILDDIR = build
BINDIR = $(BUILDDIR)/bin
TARGET = $(BINDIR)/app

SOURCES = $(wildcard $(SRCDIR)/*.c)
OBJECTS = $(patsubst $(SRCDIR)/%.c, $(BUILDDIR)/%.o, $(SOURCES))

.PHONY: all build run debug clean

build: all

all: $(TARGET)

$(TARGET): $(OBJECTS)
	@echo "Compiling Binary"
	mkdir -p $(BUILDDIR) $(BINDIR)
	$(CC) $(CFLAGS) -o $@ $^

$(BUILDDIR)/%.o: $(SRCDIR)/%.c
	@echo "Building Objects"
	$(CC) $(CFLAGS) -c -o $@ $<

run: $(TARGET)
	@echo "Launching executable $(TARGET)..."
	./$(TARGET)

debug: $(TARGET)

clean:
	rm -rf $(BUILDDIR)/*.o $(TARGET)
