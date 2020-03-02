package activiti;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.api.process.model.ProcessDefinition;
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





}
