package activiti.controller;

import activiti.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器：任务执行
 */
@RestController
public class MyController {
    @Autowired
    private ProcessRuntime processRuntime;
    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private SecurityUtil securityUtil;

    //查询任务并执行
    @RequestMapping("/task")
    public void testTask(){
        //securityUtil.logInAs("liuchuan");//指定用户认证信息
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
