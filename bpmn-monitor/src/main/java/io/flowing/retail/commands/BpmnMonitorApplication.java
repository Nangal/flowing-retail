package io.flowing.retail.commands;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

@SpringBootApplication
@EnableAutoConfiguration
@EnableProcessApplication
public class BpmnMonitorApplication  {

  public static void main(String... args) {
    ChannelStartup.startup( //
        "BPMN_MONITOR", //
        new KafkaSender(), //
        new KafkaChannelConsumer("bpmn-monitor"), //
        new BpmnMonitorEventConsumer());

    SpringApplication.run(BpmnMonitorApplication.class, args);

    // now install license and add default users to Camunda to be ready-to-go
    ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();    
    LicenseHelper.setLicense(engine);
    UserGenerator.createDefaultUsers(engine);
  }

}
