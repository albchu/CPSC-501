/*  Include files  */
#include <stdio.h>
#include <sndfile.h>
#include <sndfile.hh>
#include <time.h> 
#include <stdlib.h>
#include <iostream>

using namespace std;

#define SWAP(a,b)  tempr=(a);(a)=(b);(b)=tempr

/*  Function prototypes  */
void convolve(float x[], int N, float h[], int M, float y[], int P);
void print_vector(char *title, float x[], int N);

int read_wav(const char *file_name, float *& buf)
{
	SNDFILE *sf;
	SF_INFO info;
	int num_channels;
	int num, num_items;
	//float *buf;
	int f, sr, c;
	int i, j;
	FILE *out;

	/* Open the WAV file. */
	info.format = 0;
	cout << "Reading: " << file_name << endl;
	sf = sf_open(file_name, SFM_READ, &info);
	if (sf == NULL)
	{
		printf("Failed to open the file.\n");
		exit(-1);
	}
	/* Print some of the info, and figure out how much data to read. */
	f = info.frames;
	sr = info.samplerate;
	c = info.channels;
	printf("frames=%d\n", f);
	printf("samplerate=%d\n", sr);
	printf("channels=%d\n", c);
	num_items = f*c;
	printf("num_items=%d\n", num_items);
	/* Allocate space for the data to be read, then read it. */
	//buf = (int *)malloc(num_items*sizeof(int));
	buf = new float[info.channels * info.frames];
	num = sf_read_float(sf, buf, num_items);
	sf_close(sf);
	printf("Read %d items\n", num);
	return num;
}

int write_wav(const char *file_name, float *& buf, int num)
{
	const int format = SF_FORMAT_WAV | SF_FORMAT_PCM_16;
	const int channels = 1;
	const int sampleRate = 48000;

	SndfileHandle outfile(file_name, SFM_WRITE, format, channels, sampleRate);
	if (!outfile) return -1;

	const int format = SF_FORMAT_WAV | SF_FORMAT_PCM_16;
	//  const int format=SF_FORMAT_WAV | SF_FORMAT_FLOAT;
	const int channels = 1;
	const int sampleRate = 48000;

	SndfileHandle outfile(file_name, SFM_WRITE, format, channels, sampleRate);
	if (!outfile) return -1;

	return outfile.write(buf, num);
}

//  The four1 FFT from Numerical Recipes in C,
//  p. 507 - 508.
//  Note:  changed float data types to double.
//  nn must be a power of 2, and use +1 for
//  isign for an FFT, and -1 for the Inverse FFT.
//  The data is complex, so the array size must be
//  nn*2. This code assumes the array starts
//  at index 1, not 0, so subtract 1 when
//  calling the routine (see main() below).

void four1(double data[], int nn, int isign)
{
	unsigned long n, mmax, m, j, istep, i;
	double wtemp, wr, wpr, wpi, wi, theta;
	double tempr, tempi;

	n = nn << 1;
	j = 1;

	for (i = 1; i < n; i += 2) {
		if (j > i) {
			SWAP(data[j], data[i]);
			SWAP(data[j + 1], data[i + 1]);
		}
		m = nn;
		while (m >= 2 && j > m) {
			j -= m;
			m >>= 1;
		}
		j += m;
	}

	mmax = 2;
	while (n > mmax) {
		istep = mmax << 1;
		theta = isign * (6.28318530717959 / mmax);
		wtemp = sin(0.5 * theta);
		wpr = -2.0 * wtemp * wtemp;
		wpi = sin(theta);
		wr = 1.0;
		wi = 0.0;
		for (m = 1; m < mmax; m += 2) {
			for (i = m; i <= n; i += istep) {
				j = i + mmax;
				tempr = wr * data[j] - wi * data[j + 1];
				tempi = wr * data[j + 1] + wi * data[j];
				data[j] = data[i] - tempr;
				data[j + 1] = data[i + 1] - tempi;
				data[i] += tempr;
				data[i + 1] += tempi;
			}
			wr = (wtemp = wr) * wpr - wi * wpi + wr;
			wi = wi * wpr + wtemp * wpi + wi;
		}
		mmax = istep;
	}
}


// Takes the results from a DFT or FFT, and
// calculates and displays the amplitudes of
// the harmonics.

void postProcessComplex(double x[], int N)
{
	int i, k, j;
	double *amplitude, *result;

	// Allocate temporary arrays
	amplitude = (double *)calloc(N, sizeof(double));
	result = (double *)calloc(N, sizeof(double));

	// Calculate amplitude
	for (k = 0, i = 0; k < N; k++, i += 2) {
		// Scale results by N
		double real = x[i] / (double)N;
		double imag = x[i + 1] / (double)N;
		// Calculate amplitude
		amplitude[k] = sqrt(real * real + imag * imag);
	}

	// Combine amplitudes of positive and negative frequencies
	result[0] = amplitude[0];
	result[N / 2] = amplitude[N / 2];
	for (k = 1, j = N - 1; k < N / 2; k++, j--)
		result[k] = amplitude[k] + amplitude[j];


	// Print out final result
	printf("Harmonic \tAmplitude\n");
	printf("DC \t\t%.6f\n", result[0]);
	for (k = 1; k <= N / 2; k++)
		printf("%-d \t\t%.6f\n", k, result[k]);
	printf("\n");

	// Free up memory used for arrays
	free(amplitude);
	free(result);
}
/*****************************************************************************
*
*    Function:     main
*
*    Description:  Tests the convolve function with various input signals
*
*****************************************************************************/
int main(int argc, char* argv[])
{

	if (argc != 4)
	{
		cerr << "Expected 3 arguments: <inputfile> <IRfile> <outputfile>, but got " << argc << endl;
		exit(0);
	}

	const char * input_file = argv[1];
	const char * input_response_file = argv[2];
	const char * output_file = argv[3];
	cout << "Input file: " << input_file << endl;
	cout << "Input Response file: " << input_response_file << endl;
	cout << "Output file: " << output_file << endl;

	float *input_signal, *impulse_response;
	int input_size = read_wav(input_file, input_signal);
	int impulse_size = read_wav(input_response_file, impulse_response);

	/*  Print out the input signal to the screen  */
	//print_vector("Original input signal", buf, num);
	
	// Get the number of segments necessary to split the input signal into
	int num_segments = (input_size + impulse_size - 1) / impulse_size;	// Get the division ceiling

	int remaining_input_size = input_size;
	for (int i = 0; i < num_segments; i++)
	{
		int segment_size;
		if (remaining_input_size < impulse_size)	// If there are less signals than impulse size left to parse, we should just use that data for the segment size
			segment_size = remaining_input_size;
		else
		{
			segment_size = impulse_size - 1;
			remaining_input_size -= segment_size;
		}
		double *segment = new double[segment_size * 2];		// Times 2 for padding
	}
		//int num_segments = input_size /





	///*  Set the expected size of the output signal  */
	//int output_size = input_size + impulse_size - 1;
	//float * output_signal = new float[output_size];
	///*  Do the convolution, and print the output signal  */
	//clock_t t;
	//t = clock();
	//convolve(input_signal, input_size, impulse_response, impulse_size, output_signal, output_size);
	//t = clock() - t;
	//cout << "Total amount of time spent convoluting: " << ((float)t) / CLOCKS_PER_SEC << " seconds" << endl;
	//write_wav(output_file, output_signal, output_size);

	/*  End of program  */
	return 0;
}


/*****************************************************************************
*
*    Function:     convolve
*
*    Description:  Convolves two signals, producing an output signal.
*                  The convolution is done in the time domain using the
*                  "Input Side Algorithm" (see Smith, p. 112-115).
*
*    Parameters:   x[] is the signal to be convolved
*                  N is the number of samples in the vector x[]
*                  h[] is the impulse response, which is convolved with x[]
*                  M is the number of samples in the vector h[]
*                  y[] is the output signal, the result of the convolution
*                  P is the number of samples in the vector y[].  P must
*                       equal N + M - 1
*
*****************************************************************************/
void convolve(float x[], int N, float h[], int M, float y[], int P)
{
  int n, m;

  /*  Make sure the output buffer is the right size: P = N + M - 1  */
  if (P != (N + M - 1)) {
    printf("Output signal vector is the wrong size\n");
    printf("It is %-d, but should be %-d\n", P, (N + M - 1));
    printf("Aborting convolution\n");
    return;
  }

  /*  Clear the output buffer y[] to all zero values  */  
  cout << "Clearing the output buffer to all zero values...";
  for (n = 0; n < P; n++)
    y[n] = 0.0;
  cout << "Completed" << endl;
  /*  Do the convolution  */
  /*  Outer loop:  process each input value x[n] in turn  */
  cout << "Convolution loop: " << endl;
  for (n = 0; n < N; n++) {
	  printf("Percent Completed: %.2f\r", ((float)n) / N * 100);
	  //cout << ((float)n)/N * 100 << "%" << "\r";
    /*  Inner loop:  process x[n] with each sample of h[]  */
    for (m = 0; m < M; m++)
      y[n+m] += x[n] * h[m];
  }
  cout << endl;
}



/*****************************************************************************
*
*    Function:     print_vector
*
*    Description:  Prints the vector out to the screen
*
*    Parameters:   title is a string naming the vector
*                  x[] is the vector to be printed out
*                  N is the number of samples in the vector x[]
*
*****************************************************************************/

void print_vector(char *title, float x[], int N)
{
  int i;

  printf("\n%s\n", title);
  printf("Vector size:  %-d\n", N);
  printf("Sample Number \tSample Value\n");
  for (i = 0; i < N; i++)
    printf("%-d\t\t%f\n", i, x[i]);
}
