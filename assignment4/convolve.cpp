/*  Include files  */
#include <stdio.h>
#include <sndfile.h>
#include <sndfile.hh>
#include <time.h>
#include <algorithm>
#include <iostream>
using namespace std;

#define SWAP(a,b)  tempr=(a);(a)=(b);(b)=tempr

/*  Function prototypes  */
void convolve(double input_signal[], int input_size, double impulse_response[], int impulse_size, double output_signal[], int output_size, int pow2);
void print_vector(char *title, float x[], int N);


void FFTScaling(double *& signal, int N)
{
	int k;
	int i;
	for (k = 0, i = 0; k < N; k++, i += 2) {
		signal[i] /= (float)N;
		signal[i + 1] /= (float)N;
	}
}
int read_wav(const char *file_name, double *& buf)
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
	buf = new double[info.channels * info.frames];
	num = sf_read_double(sf, buf, num_items);
	sf_close(sf);
	printf("Read %d items\n", num);
	return num;
}

int write_wav(const char *file_name, double *& buf, int num)
{
	const int format = SF_FORMAT_WAV | SF_FORMAT_PCM_16;
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
//
//// Takes the results from a DFT or FFT, and
//// calculates and displays the amplitudes of
//// the harmonics.
//
//void postProcessComplex(double *& x, int N)
//{
//	int i, k, j;
//	double *amplitude, *result;
//
//	// Allocate temporary arrays
//	amplitude = (double *)calloc(N, sizeof(double));
//	result = (double *)calloc(N, sizeof(double));
//
//	// Calculate amplitude
//	for (k = 0, i = 0; k < N; k++, i += 2) {
//		// Scale results by N
//		double real = x[i] / (double)N;
//		double imag = x[i + 1] / (double)N;
//		// Calculate amplitude
//		amplitude[k] = sqrt(real * real + imag * imag);
//	}
//
//	// Combine amplitudes of positive and negative frequencies
//	result[0] = amplitude[0];
//	result[N / 2] = amplitude[N / 2];
//	for (k = 1, j = N - 1; k < N / 2; k++, j--)
//		result[k] = amplitude[k] + amplitude[j];
//
//
//	//// Print out final result
//	//printf("Harmonic \tAmplitude\n");
//	//printf("DC \t\t%.6f\n", result[0]);
//	//for (k = 1; k <= N / 2; k++)
//	//	printf("%-d \t\t%.6f\n", k, result[k]);
//	//printf("\n");
//
//	// Free up memory used for arrays
//	free(amplitude);
//	free(result);
//}
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

	double *input_signal, *impulse_response;
	int input_size = read_wav(input_file, input_signal);
	int impulse_size = read_wav(input_response_file, impulse_response);

	/*  Print out the input signal to the screen  */
	//print_vector("Original input signal", buf, num);

	/*  Set the expected size of the output signal  */
	int max_size = max(input_size, impulse_size);

	int pow2 = (int)log2(max_size) + 1;
	pow2 = pow(2, pow2);

	int output_size = input_size + impulse_size - 1;
	//double * output_signal = new double[2 * output_size];
	double * output_signal = new double[output_size];

	//outputSignal = new float[outputSignalSize];

	/*  Do the convolution, and print the output signal  */
	clock_t t;
	t = clock();
	convolve(input_signal, input_size, impulse_response, impulse_size, output_signal, output_size, pow2);
	t = clock() - t;
	cout << "Total amount of time spent convoluting: " << ((float)t) / CLOCKS_PER_SEC << " seconds" << endl;
	write_wav(output_file, output_signal, output_size);

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
void convolve(double input_signal[], int input_size, double impulse_response[], int impulse_size, double output_signal[], int output_size, int pow2)
{

	double *hComplex = new double[2 * pow2];
	double *xComplex = new double[2 * pow2];
	double *yComplex = new double[2 * pow2];


	int i = 0;

	//set hComplex with 0's
	for (i = 0; i < 2 * pow2; i++) {
		hComplex[i] = 0.0;
	}

	//set xComplex with 0's
	for (i = 0; i < 2 * pow2; i++) {
		xComplex[i] = 0.0;
	}

	//padding the complex number with 0 and the real number with original value for h
	for (i = 0; i < impulse_size; i++) {
		hComplex[2 * i] = impulse_response[i];
	}

	//padding the complex number with 0 and the real number with original value for x
	for (i = 0; i < input_size; i++) {
		xComplex[2 * i] = input_signal[i];
	}


	four1(hComplex, pow2, 1);
	four1(xComplex, pow2, 1);

	for (i = 0; i < output_size; i++) {
		yComplex[i * 2] = xComplex[i] * hComplex[i] - xComplex[i + 1] * hComplex[i + 1];
		yComplex[i * 2 + 1] = xComplex[i + 1] * hComplex[i] + xComplex[i] * hComplex[i + 1];
	}

	four1(yComplex - 1, pow2, -1);
	//FFTScaling(yComplex, pow2);

	// removing padding
	for (i = 0; i < output_size; i++) {
		output_signal[i] = yComplex[i * 2];
	}
	//postProcessComplex(output_signal, output_size);
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
