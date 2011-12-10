Julez is a lightweight Java toolkit for building performance and concurrency tests that run via junit. Because the tests are **just** junit tests you can develop and run them as you would any other junit test, from your IDE, from your build or from your CI server. Providing your CI server supports it, you can even schedule them to run over night or farm them out to multiple CI nodes to increase load on your target system.

The Julez core is a concurrent scenario runner. It handles the multi-threading, you provide the scenarios. A Scenario is a "Runnable" Java class, but other than that has no restrictions. You are free write scenarios using any other library you wish. You might for example want to soak test a web application using a combination of WebDriver and JBehave, it's entirely up to you. 

Scenarios can be pre-queued (assuming sufficient memory is available), or generated on demand. By wrapping the scenario queue in one or more of adapters, it can be throttled, shortened or topped up when a threshold is reached.

In addition to multi-threading and throttling, Julez includes an event model for reporting test results. By wiring up the appropriate set of handlers, test events (e.g. pass / fail / error) can be filtered, asynchronously persisted, routed to a remote server etc. Events can also be correlated by test run (or any other attribute you want) to facilitate trending.

What Julez intentionally isn't is a standalone product. Instead, think of it as Lego for writing performance and concurrency tests, that you assemble and execute just as you do any other Java application.
