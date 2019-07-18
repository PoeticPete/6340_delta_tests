# Quickstart
1. Clone the repo into your "delta" directory
2. cd 6340_delta_tests
3. ./run_tests.sh

# What tests are run
1. text files with a single "bad" (non-ascii) character. Largest text file is 1000 chars per line, with 1000 lines (long_failing_text.txt was 43 lines, with an average of 217 characters each line). Files between 1x1 to 100x100 (100 chars per line, 100 lines) are covered.
2. Multiple non-ascii characters are used. I literally just held opt+keys to get these non-ascii chars on a mac.
3. Line granularity is run using the DeltaDebug class
`dd.deltaDebug(false, "java SecretCoder", inputFile, errMsg, resultFile);`
4. Char granularity is run using the DeltaDebug class
`dd.deltaDebug(false, "java SecretCoder", inputFile, errMsg, resultFile);`
5. the `diff` command is used to determine differences
