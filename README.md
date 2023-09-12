# Bach Linh Order Shop

## Getting started

To working with this project. I recommend set up your workspace follow these step.

## Check list

- [ ] [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) Java 17 and set up
  your environment.
    - After download jdk set up JAVA_HOME following command:
        -
        Window: [Set up java home](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html)
        - Linux:
            ```
            export JAVA_HOME=/path/to/downloaded/jdk
            source /etc/environment
            java -version
            ``` 
- [ ] Install git. If you use linux, git is already installed in your computer.
- [ ] Clone repo. Open terminal and run command.
    - Window: ```cd /d path/to/your/folder```
    - Linux: ```cd /path/to/your/folder```
  ```
  git clone -b your-branch https://github.com/minhquan490/Order-Shop.git
  ```
- [ ] Install gradle for config project's repositories.
  See [how to install gradle](https://www.tutorialspoint.com/gradle/gradle_installation.htm)

- [ ] Install ide what you like.
    - IntelliJ: [Download](https://www.jetbrains.com/toolbox-app/)
    - Eclipse: [Download](https://www.eclipse.org/downloads/)
    - Spring tool suite 4: [Download](https://spring.io/tools)

## Project contents

### Packages and folders convention

| Folder               | Package                                 |
|----------------------|-----------------------------------------|
| annotation-processor | com.bachlinh.order.annotation.processor |
| aot                  | com.bachlinh.order.aot                  |
| batch                | com.bachlinh.order.batch                |
| common/analyzer      | com.bachlinh.order.analyzer             |
| common/annotation    | com.bachlinh.order.annotation           |
| common/core          | com.bachlinh.order.core                 |
| common/data-transfer | com.bachlinh.order.dto                  |
| common/entity        | com.bachlinh.order.entity               |
| common/environment   | com.bachlinh.order.environment          |
| common/exception     | com.bachlinh.order.exception            |
| common/handler       | com.bachlinh.order.handler              |
| common/mail          | com.bachlinh.order.mail                 |
| common/repository    | com.bachlinh.order.repository           |
| common/security      | com.bachlinh.order.security             |
| common/setup         | com.bachlinh.order.setup                |
| common/trigger       | com.bachlinh.order.trigger              |
| common/utils         | com.bachlinh.order.utils                |
| common/validate      | com.bachlinh.order.validate             |
| crawler              | com.bachlinh.order.crawler              |
| service              | com.bachlinh.order.service              |
| web                  | com.bachlinh.order.web                  |

### The meaning of packages

- com.bachlinh.order.annotation.processor: Place all code execute when project compiling.
- com.bachlinh.order.aot: Spring boot 3 aot processing code for collect graalvm metadata use for native compile phase.
- com.bachlinh.order.batch: Batch job processing. All job subscribe will be executed when deadline.
- com.bachlinh.order.analyzer: Analyzer use for search engine only. Contains code interact with coccoc language analyze
  tool.
- com.bachlinh.order.annotation: Contains annotations of project.
- com.bachlinh.order.core: Core system heart of project.
- com.bachlinh.order.dto: Dto mapping system. Support map entity to dto or dto to entity.
- com.bachlinh.order.entity: Contains logic processing in domain system.
- com.bachlinh.order.environment: Environment metadata of project.
- com.bachlinh.order.exception: All project's exceptions placing in here.
- com.bachlinh.order.handler: Define route, router, controller and define how this project can interact with network.
  When developing api, you <b>must</b> extends <b>AbstractController</b> place in here.
- com.bachlinh.order.mail: Contains sending email logic.
- com.bachlinh.order.repository: Project's repository layer, like DAO layer.
- com.bachlinh.order.security: Use for security reason.
- com.bachlinh.order.setup: Contains logic that will be executed when project start.
- com.bachlinh.order.trigger: Contains trigger that will be applied when interact with entity like save, update, delete,
  etc...
- com.bachlinh.order.utils: Util package.
- com.bachlinh.order.validate: Define validate logic. When insert or update, data will be validated two time. One is
  before controller receive request and one before insert or update database.
- com.bachlinh.order.crawler: Contains bot logic.
- com.bachlinh.order.service: Wrapping logic. Wrap IoC container for easy migrating to new IoC container.
- com.bachlinh.order.web: Web application package.

### System execute phase

- Phase 1: Compile phase. All code and logic in folder annotation-processor and aot will be executed in this phase.
- Phase 2: Start up. System start up phase, the configuration and all system require components will be initialized in
  this phase. Use [Reflection API](https://www.oracle.com/technical-resources/articles/java/javareflection.html) in this
  phase is allowed.
- Phase 3: Run time
  phase. [Reflection API](https://www.oracle.com/technical-resources/articles/java/javareflection.html) will be
  forbidden in this phase.
- Phase 4: Before terminated.

### System business

Refer to other documentation.

## Thanks for reading.