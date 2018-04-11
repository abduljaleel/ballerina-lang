#At the command line, navigate to the directory that contains the 
#`.bal` file and run the `ballerina test` command.
$ ballerina test testerina-groups.bal --groups g1,g2
---------------------------------------------------------------------------
    T E S T S
---------------------------------------------------------------------------
---------------------------------------------------------------------------
Running Tests of Package: .
---------------------------------------------------------------------------
I'm in test belonging to g1 and g2!
I'm in test belonging to g1!

Tests run: 2, Passed: 2, Failures: 0, Skipped: 0 - in TestSuite


---------------------------------------------------------------------------
Results:

Tests run: 2, Passed: 2, Failures: 0, Skipped: 0
---------------------------------------------------------------------------
Summary:
................................................................... SUCCESS
---------------------------------------------------------------------------


$ ballerina test testerina-groups.bal --groups g1
---------------------------------------------------------------------------
    T E S T S
---------------------------------------------------------------------------
---------------------------------------------------------------------------
Running Tests of Package: .
---------------------------------------------------------------------------
I'm in test belonging to g1 and g2!

Tests run: 1, Passed: 1, Failures: 0, Skipped: 0 - in TestSuite


---------------------------------------------------------------------------
Results:

Tests run: 1, Passed: 1, Failures: 0, Skipped: 0
---------------------------------------------------------------------------
Summary:


................................................................... SUCCESS
---------------------------------------------------------------------------

$ ballerina test testerina-groups.bal --groups default
---------------------------------------------------------------------------
    T E S T S
---------------------------------------------------------------------------
---------------------------------------------------------------------------
Running Tests of Package: .
---------------------------------------------------------------------------
I'm the ungrouped test.

Tests run: 1, Passed: 1, Failures: 0, Skipped: 0 - in TestSuite


---------------------------------------------------------------------------
Results:

Tests run: 1, Passed: 1, Failures: 0, Skipped: 0
---------------------------------------------------------------------------
Summary:
................................................................... SUCCESS
---------------------------------------------------------------------------

$ ballerina test testerina-groups.bal --exclude-groups g2

---------------------------------------------------------------------------
    T E S T S
---------------------------------------------------------------------------
---------------------------------------------------------------------------
Running Tests of Package: .
---------------------------------------------------------------------------
I'm the ungrouped test.
I'm in test belonging to g1!

Tests run: 2, Passed: 2, Failures: 0, Skipped: 0 - in TestSuite

---------------------------------------------------------------------------
Results:

Tests run: 2, Passed: 2, Failures: 0, Skipped: 0
---------------------------------------------------------------------------
Summary:
................................................................... SUCCESS
---------------------------------------------------------------------------
