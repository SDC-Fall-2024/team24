TARGET = App.java
SRCDIR = src
BUILDDIR = build
BINDIR = bin
JAR = $(BINDIR)/App.jar
MANIFEST = $(BUILDDIR)/MANIFEST.MF

SOURCES = $(shell fd .java)
CLASS_FILES = $(patsubst $(SRCDIR)/%.java, $(BUILDDIR)/%.class, $(SOURCES))
PATHS := $(shell fd -t 'dir' | grep 'src' | sed 's/\/$$//g' | paste -sd ':' -)


.PHONY: all build run clean test info

all: build

build: $(CLASS_FILES)

$(BUILDDIR)/%.class: $(SRCDIR)/%.java
	@echo "Building .class files"
	@mkdir -p $(BUILDDIR)  # Create build directory if it doesn't exist
	@javac -cp $(PATHS) -d $(BUILDDIR) $<

jar: build
	@echo "Creating Jar file $(JAR)"
	@jar cfm $(JAR) $(MANIFEST) -C $(BUILDDIR) .

clean:
	@echo "Cleaning build artifacts"
	@rm -rf $(BUILDDIR)/*.class $(JAR)

run: jar
	@echo "Launching $(JAR)"
	@java -jar $(JAR)
