package activiti;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SpringBoot与Junit整合，测试流程定义相关操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiTest {
    @Autowired
    private ProcessRuntime processRuntime;//流程定义操作

    @Autowired
    private TaskRuntime taskRuntime;//任务操作

    @Autowired
    private SecurityUtil securityUtil;//SpringSecurity相关工具类

    /**
     * 查看流程定义
     */
    @Test
    public void contextLoads(){
        securityUtil.logInAs("sanjing");//用户认证
        //流程定义信息的查看  bpmn文件放在/resources/processes/下，会自动部署到activiti数据库中，可在act_ge_bytearray中查看部署的流程信息
        Page<ProcessDefinition> processDefinitionPages=processRuntime.processDefinitions(Pageable.of(0,10));
        System.out.println(processDefinitionPages.getTotalItems());//已部署的流程个数

        //得到当前部署的每个流程定义信息
        for(ProcessDefinition pd:processDefinitionPages.getContent()){
            System.out.println(pd);
        }

    }


    //流程定义实例启动
    @Test
    public void testStartInstance(){
        securityUtil.logInAs("other");//SpringSecurity框架的用户认证
        //启动流程定义key为SlamDunk的一个实例
        ProcessInstance processInstance=processRuntime.start(ProcessPayloadBuilder
        .start()
        .withProcessDefinitionKey("SlamDunk")//流程定义key(像类名一样)
        .build());//底层启动调用的是RuntimeService的start方法

        System.out.println("流程实例id"+processInstance.getId());

    }


    /**
     * 查询任务: taskRuntime.tasks() ->用户:liuchuan 能查询到负责人为该用户、或候选人为该用户、或者候选人组包含该用户的所有要被完成的任务
     * 拾取任务: taskRuntime.claim() ->根据查询到的任务id拾取该任务
     * 完成任务: taskRuntime.complete() ->根据查询到的任务id完成任务
     */
    @Test
    public void testTask(){
        securityUtil.logInAs("liuchuan");//指定用户认证信息
        Page<Task> taskPages=taskRuntime.tasks(Pageable.of(0,10));//分页查询任务列表
        if(taskPages.getTotalItems()>0){
            //有任务，能查询到负责人为该用户、或候选人为该用户、或者候选人组包含该用户的所有要被完成的任务
            for(Task task:taskPages.getContent()){
                //遍历任务并输出
                System.out.println("任务1: "+task);

                //拾取任务 (用户：liuchuan)，这里拾取的candidateGroups="activitiTeam"的任务，用户属于该组
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());

                //完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());

            }
        }

        //再次查询新任务
        taskPages=taskRuntime.tasks(Pageable.of(0,10));
        if(taskPages.getTotalItems()>0){
            //有任务，能查询到负责人为该用户、或候选人为该用户、或者候选人组包含该用户的所有要被完成的任务
            for(Task task:taskPages.getContent()){
                //遍历任务并输出
                System.out.println("任务2: "+task);

            }
        }


    }





}
