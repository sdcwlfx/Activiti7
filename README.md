# Activiti7
## Activiti7与Spring的整合
	1. maven工程，pom.xml添加依赖
	2. 创建aactiviti与spring整合的配置文件:activiti-spring.xml->数据源配置、流程引擎、事务管理器等
	
	
	
## Activiti7与SpringBoot的整合
	1. pom.xml依赖
	2. SpringSecurity的用户信息配置类DemoApplicationConfiguration
	3. SpringSecurity的用户信息验证类SecurityUtil
	4. 链接数据库的application.yml文件放在resource下
		解决：生成activiti数据库中只有17张表,无历史表(8张)
			在application.yml中添加：
			spring:
				activiti:
					historyLevel: audit
					db-history-used: true 开启数据库历史表记录,默认是关闭的，生成的activiti数据库只有17张表，无历史表(8张)
					
	5. 流程定义.bpmn文件 
		在resources/processes/下，会自动完成部署，无需代码部署。在act_ge_bytearray中可找到bpmn文件信息
		可以设置负责人、候选人、用户组(候选人一定是同一组的)
	6. 启动流程定义实例(此时act_ru_*中会有正在运行的信息)(ActivitiTest.testStartInstance函数)
		启动成功后，可以在act_ru_task(excution_id字段)表中看到正在运行等待被执行的任务(结点)
		在act_ru_execution(正在启动的活动，包括开始事件、任务)可查看启动的活动，包括开始事件、当前要完成的任务，可以查看启动用户
	7. 查询任务、拾取任务、完成任务（ActivitiTest.testTask函数）
		首先：有SpringSecurity框架的用户认证->指明了用户
		查询任务: taskRuntime.tasks() ->能查询到负责人为该用户、或候选人为该用户、或者候选人组包含该用户的所有要被完成的任务
		拾取任务：taskRuntime.claim() ->根据查询到的任务id拾取该任务
		完成任务：taskRuntime.complete() ->根据查询到的任务id完成任务
		
		最后一个任务完成结束后，流程实例结束，act_ru_task表、act_ru_execution均无该实例信息(删除了)
		
		
	测试：
		流程任务SG、SF、PF三个结点的候选人所属组均为"activitiTeam",用户"liuchuan"属于该组，用户"other"不属于该组 ->
		所以LogAS("liuchuan")能启动流程实例(testStartInstance函数)、也能查询到三个任务并完成任务(testTask函数)。
		而LogAS("other")能启动流程实例(testStartInstance函数), 但查询不到三个任务，也就无法拾取并执行任务。因为三个任务所属候选组不包含用户"other"。
				
	
## Activiti7与SpringBoot、SpringMVC的整合
	1. java/activiti目录下创建controller文件，并创建MyController类，实现查询并完成任务逻辑
	2. 编写Actviti7DemoApplication(SpringBoot引导类)，并注解不再使用SpringSecurity用户认证，启动该类进行测试(前提是必须有流程实例正在运行)
		浏览器中输入：localhost:8080/task (task为MyController类中注解的响应页面),输入用户名和密码(DemoApplicationConfiguration中设置)，
		查看控制台输出信息：会显示已经完成的任务，以及获取的新的待完成的任务(也可查看act_ru_task表查看当前要等待被完成的任务)
		
