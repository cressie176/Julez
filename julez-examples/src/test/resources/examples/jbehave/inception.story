Scenario: Jbehave invoking Julez invoking JBehave

Given a throughput monitor
And a result monitor
When I run 1000 calculation scenarios from 3 threads
Then the minimum throughput should be 100 calculations per second
And 100% of scenarios should be successful