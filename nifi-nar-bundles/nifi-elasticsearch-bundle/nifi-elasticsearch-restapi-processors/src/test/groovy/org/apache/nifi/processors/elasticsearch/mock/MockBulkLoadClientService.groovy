/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License") you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nifi.processors.elasticsearch.mock


import org.apache.nifi.elasticsearch.IndexOperationRequest
import org.apache.nifi.elasticsearch.IndexOperationResponse

class MockBulkLoadClientService extends AbstractMockElasticsearchClient {
    IndexOperationResponse response
    Closure evalClosure
    Closure evalParametersClosure

    @Override
    IndexOperationResponse bulk(List<IndexOperationRequest> items, Map<String, String> requestParameters) {
        if (throwRetriableError) {
            throw new MockElasticsearchException(true, false)
        } else if (throwFatalError) {
            throw new MockElasticsearchException(false, false)
        }

        if (evalClosure) {
            evalClosure.call(items)
        }

        if (evalParametersClosure) {
            evalParametersClosure.call(requestParameters)
        }

        response
    }

    static final SAMPLE_ERROR_RESPONSE = """
{
  "took" : 18,
  "errors" : true,
  "items" : [
    {
      "index" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "1",
        "_version" : 4,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 4,
        "_primary_term" : 1,
        "status" : 200
      }
    },
    {
      "create" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "create" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "3",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 3,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "delete": {
        "_index": "test",
        "_type" : "_doc",
        "_id": "4",
        "_version": 1,
        "result": "not_found",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no" : 1,
        "_primary_term" : 2,
        "status": 404
      }
    },
    {
      "index" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "5",
        "status" : 400,
        "error" : {
          "type" : "mapper_parsing_exception",
          "reason" : "failed to parse field [field2] of type [integer] in document with id '5'",
          "caused_by" : {
            "type" : "number_format_exception",
            "reason" : "For input string: 20abc"
          }
        }
      }
    },
    {
      "index" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "6",
        "status" : 400,
        "error" : {
          "type" : "mapper_parsing_exception",
          "reason" : "failed to parse field [field3] of type [geopoint] in document with id '6'",
          "caused_by" : {
            "type" : "number_format_exception",
            "reason" : "For input string: 213,456.9"
          }
        }
      }
    },
    {
      "index" : {
        "_index" : "test",
        "_type" : "_doc",
        "_id" : "7",
        "status" : 400,
        "error" : {
          "type" : "some_other_exception",
          "reason" : "failed to index document with id '7' due to some other reason",
          "caused_by" : {
            "type" : "random_exception",
            "reason" : "For input string: unit test"
          }
        }
      }
    }
  ]
}
        """
}
