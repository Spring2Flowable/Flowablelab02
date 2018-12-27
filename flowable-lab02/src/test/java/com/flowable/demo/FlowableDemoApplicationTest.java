package com.flowable.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:application.yml")
@SpringBootTest
public class FlowableDemoApplicationTest {
	@Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private FormRepositoryService formRepositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 流程以及表单的部署
     */
    @Test
    public void deployTest(){
    	Deployment deployment = repositoryService.createDeployment()
                .name("表单流程")
                .addClasspathResource("processes/test-form.bpmn20.xml")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
                deploymentId(deployment.getId())
                .singleResult();
        String processDefinitionId = processDefinition.getId();
        FormDeployment formDeployment = formRepositoryService.createDeployment()
                .name("definition-one")
                .addClasspathResource("forms/test.form")
                .parentDeploymentId(deployment.getId())
                .deploy();
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().deploymentId(formDeployment.getId()).singleResult();
        String formDefinitionId = formDefinition.getId();



        //启动实例并且设置表单的值
        String outcome = "shareniu";
        Map<String, Object> formProperties;
        formProperties = new HashMap<>();
        formProperties.put("reason", "家里有事");
        formProperties.put("startTime", new Date());
        formProperties.put("endTime", new Date());
        String processInstanceName = "shareniu";
        runtimeService.startProcessInstanceWithForm(processDefinitionId, outcome, formProperties, processInstanceName);
        HistoricProcessInstanceEntity historicProcessInstanceEntity = (HistoricProcessInstanceEntity )historyService.createHistoricProcessInstanceQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        String processInstanceId = historicProcessInstanceEntity.getProcessInstanceId();



        //查询表单信息
        FormInfo fm = runtimeService.getStartFormModel(processDefinitionId, processInstanceId);
        System.out.println(fm.getId());
        System.out.println(fm.getKey());
        System.out.println(fm.getName());
        System.err.println(fm.getVersion());
//        List<FormField> fields = fm.getFields();
//        for (FormField ff : fields) {
//            System.out.println("######################");
//            System.out.println(ff.getId());
//            System.out.println(ff.getName());
//            System.out.println(ff.getType());
//            System.out.println(ff.getPlaceholder());
//            System.out.println(ff.getValue());
//            System.out.println("######################");
//
//        }


        //查询个人任务并填写表单
        Map<String, Object> formProperties2 = new HashMap<>();
        formProperties2.put("reason", "家里有事2222");
        formProperties2.put("startTime", new Date());
        formProperties2.put("endTime", new Date());
        formProperties2.put("days", "3");
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        String taskId = task.getId();
        String outcome2="牛哥";
        taskService.completeTaskWithForm(taskId, formDefinitionId, outcome2, formProperties2);

        //获取个人任务表单
        FormInfo formInfo = taskService.getTaskFormModel(taskId);
        FormModel formModel = formInfo.getFormModel();
        
    }
}
