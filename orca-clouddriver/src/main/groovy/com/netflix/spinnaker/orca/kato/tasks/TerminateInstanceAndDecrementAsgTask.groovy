/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.kato.tasks

import com.netflix.spinnaker.orca.api.ExecutionStatus
import com.netflix.spinnaker.orca.Task
import com.netflix.spinnaker.orca.api.TaskResult
import com.netflix.spinnaker.orca.clouddriver.KatoService
import com.netflix.spinnaker.orca.pipeline.model.StageExecution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Deprecated
class TerminateInstanceAndDecrementAsgTask implements Task {
  @Autowired
  KatoService kato

  @Override
  TaskResult execute(StageExecution stage) {
    def taskId = kato.requestOperations([[terminateInstanceAndDecrementAsgDescription: stage.context]])
      .toBlocking()
      .first()
    TaskResult.builder(ExecutionStatus.SUCCEEDED).context([
      "notification.type"     : "terminateinstanceanddecrementasg",
      "terminate.account.name": stage.context.credentials,
      "terminate.region"      : stage.context.region,
      "kato.last.task.id"     : taskId,
      "kato.task.id"          : taskId, // TODO retire this.
      "terminate.instance.ids": [stage.context.instance],
    ]).build()
  }

}
