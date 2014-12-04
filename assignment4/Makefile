CC=g++

CFLAGS=-c
LFLAGS=-L ./lib/lib
IFLAGS=-I ./lib/include
LIBS=./lib/lib/libsndfile.a
FLAGS=-static
SOURCES=convolve.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=convolve

all: convolve

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(OBJECTS) -o $@ $(LIBS)

.cpp.o:
	$(CC) $(CFLAGS) $(LFLAGS) $(IFLAGS) $(FLAGS) $< -o $@

clean:
	rm *.o $(EXECUTABLE)
