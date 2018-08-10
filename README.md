# Docker
## Build
```
docker build -t bloomfilter .
```
## Run
Environment variables:
- input_file_name = filename of the input file, which has to be placed in resources
- use_volume = boolean which defines if the volume should be used or if the output plots should be created in the project directory
- expected_true_positives = number of true positive which is expected, use to calculate recall
- threshold = number which will be used a limit of probability if it is a match
```
docker run -it -e "expected_true_positives=15000" -e "threshold=0.8" -e "use_volume=true" -e "input_file_name=test" --mount source=vol,target=/data bloomfilter
```

```
docker run -d -e "expected_true_positives=15000" -e "threshold=0.7" -e "use_volume=true" -e "input_file_name=2_S_L_M" --mount source=vol1,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.7" -e "use_volume=true" -e "input_file_name=6_S_M_M" --mount source=vol2,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.7" -e "use_volume=true" -e "input_file_name=10_S_H_M" --mount source=vol3,target=/data bloomfilter

docker run -d -e "expected_true_positives=15000" -e "threshold=0.75" -e "use_volume=true" -e "input_file_name=2_S_L_M" --mount source=vol1,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.75" -e "use_volume=true" -e "input_file_name=6_S_M_M" --mount source=vol2,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.75" -e "use_volume=true" -e "input_file_name=10_S_H_M" --mount source=vol3,target=/data bloomfilter

.6
docker run -d -e "expected_true_positives=15000" -e "threshold=0.8" -e "use_volume=true" -e "input_file_name=2_S_L_M" --mount source=vol1,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.8" -e "use_volume=true" -e "input_file_name=6_S_M_M" --mount source=vol2,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.8" -e "use_volume=true" -e "input_file_name=10_S_H_M" --mount source=vol3,target=/data bloomfilter

docker run -d -e "expected_true_positives=15000" -e "threshold=0.85" -e "use_volume=true" -e "input_file_name=2_S_L_M" --mount source=vol1,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.85" -e "use_volume=true" -e "input_file_name=6_S_M_M" --mount source=vol2,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.85" -e "use_volume=true" -e "input_file_name=10_S_H_M" --mount source=vol3,target=/data bloomfilter

docker run -d -e "expected_true_positives=15000" -e "threshold=0.9" -e "use_volume=true" -e "input_file_name=2_S_L_M" --mount source=vol1,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.9" -e "use_volume=true" -e "input_file_name=6_S_M_M" --mount source=vol2,target=/data bloomfilter
docker run -d -e "expected_true_positives=15000" -e "threshold=0.9" -e "use_volume=true" -e "input_file_name=10_S_H_M" --mount source=vol3,target=/data bloomfilter
```

# Analyse Bloomfilters
java -jar resources/msseq.jar "resources/data" "resources/result.txt" "11" "," fft 500 0.90