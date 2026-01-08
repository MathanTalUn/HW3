#!/bin/bash
mkdir -p output_tests
passed=0
failed=0

for input in input/TEST_*.txt; do
    testname=$(basename "$input" .txt)
    echo "Running $testname..."
    java -jar SEMANT "$input" "output_tests/${testname}.txt" > /dev/null 2>&1
    
    expected=""
    if [ -f "expected_output/${testname}_Expected_Output.txt" ]; then
        expected="expected_output/${testname}_Expected_Output.txt"
    elif [ -f "expected_output/${testname}_OUTPUT.txt" ]; then
        expected="expected_output/${testname}_OUTPUT.txt"
    elif [ -f "expected_output/${testname}_Error_Expected_Output.txt" ]; then
         expected="expected_output/${testname}_Error_Expected_Output.txt"
    else
        # Fuzzy match
        base=$(echo $testname | sed 's/_Error//')
        if [ -f "expected_output/${base}_Expected_Output.txt" ]; then
            expected="expected_output/${base}_Expected_Output.txt"
        fi
    fi
    
    if [ -n "$expected" ]; then
        # Compare output, ignoring whitespace
        if diff -w "output_tests/${testname}.txt" "$expected" > /dev/null; then
            echo "  PASSED"
            passed=$((passed+1))
        else
            echo "  FAILED"
            echo "  Expected ($expected):"
            cat "$expected"
            echo "  Got:"
            cat "output_tests/${testname}.txt"
            failed=$((failed+1))
        fi
    else
        echo "  NO EXPECTED OUTPUT FOUND for $testname"
    fi
done

echo "Tests Passed: $passed"
echo "Tests Failed: $failed"
