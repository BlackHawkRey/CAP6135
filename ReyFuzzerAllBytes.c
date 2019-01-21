#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <sys/wait.h>

int main()
{
    // Variable Declarations
    FILE * fuzzedFile;
    char sourceFile[] = "./cross.jpg";
    char outputFile[] = "./test.jpg";
    char buf[1000];
    char commandBuf[200];
    char crashedFileName[50];
    char fuzzedFileName[50];
    int filePosition = -1;
    int fuzzRounds = 810;
    int fileNameCounter = 0;
    int retCode = 0;
    int ret = 0;
    int runCounter = 0;
    int status = 0;
    int randomCharacter = 0;
    time_t t, ltime, timestamp;
    ltime = time(NULL);

    // Fuzzing Code.

    srand((unsigned) time(&t));  /* randomize the initial seed */

    //Loop Through The File Changing One Byte At a Time
    //Using 2 Loops to Go Through Every Single Character From The Pool & Every Single Byte Of The File.
    //First Loop To Cover All Bytes.
    for (int i = 0; i < fuzzRounds; i++)
    {
        filePosition++;
        randomCharacter = -260;

        //Second Loop To Cover All Characters/Digits From The Pool.
        for (int w = 0; w < 520; w++) {

            //Start By Making a Clean Copy Of The Source File Everytime.
            sprintf(fuzzedFileName, "cp %s %s \n", sourceFile, outputFile );
            ret = system(fuzzedFileName);
            printf("File Copied Successfully.\n");
            fflush(stdout);

            //Load Copy of JPG File To Mutate.
            fuzzedFile = fopen(outputFile, "r+");
            if ( fuzzedFile == NULL)
            {
                puts("Could Not Open File.\n");
                exit(0);
            }

            //Adding The Changes To The File At The Specified Location.
            fseek(fuzzedFile, filePosition, SEEK_CUR);
            fputc(randomCharacter, fuzzedFile);
            fclose(fuzzedFile);

            //Increase RandomCharacter Value.
            randomCharacter++;

            //Run The Mutated File Against The JPG2BMP Converter.
            sprintf(commandBuf, "./jpg2bmp test.jpg temp.bmp");
            ret = system(commandBuf);
            printf("File Position Attempted: %d\n", filePosition);
            printf("Character Attempted: %d\n", randomCharacter);
            fflush(stdout);

            //Counter For Total Runs.
            runCounter++;

            //Code Snippet From Professor.
            wait(&status); /* wait for the program to finish */
            retCode = WEXITSTATUS(ret);
            if ( retCode == 128+11 || retCode ==128+6)  /* the target code caused segmentation fault (11) or Abort (6) */
                /* see Linux signal:  http://linux.about.com/od/commands/l/blcmdl7_signal.htm */

            {

                //Output The Error Code.
                printf("retCode=%d \n", retCode);
                timestamp = localtime(&ltime);

                //Copy File With Sucessful Bug Trigger & Provide Details.
                sprintf(crashedFileName, "cp ./test.jpg crashed-%d_%ld.jpg\n", filePosition, timestamp);
                ret = system(crashedFileName);

                printf("%s\n", crashedFileName);
                printf("Position Attempted: %d\n", filePosition);
                printf("Character Attempted: %d\n", randomCharacter);

                fileNameCounter++;

                fflush(stdout);
            }
        }
    }

    printf("Total Executions: %d \n\n", runCounter);
    return 0;
}