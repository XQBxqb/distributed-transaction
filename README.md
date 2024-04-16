<h1>分布式事务解决方案</h1>


项目由三类模块组成:module-client , module-tx , module-service<br>

模块之间关系:
module-client与module-service : 依赖与module-service的api模块，能够拿到自己要使用的接口（但是不直接调用）<br>
module-client与module-tx : module-client依赖于module-tx的api模块，能够调用发起分布式事务请求服务<br>
module-tx与module-service : module-tx 依赖于所有module-service的api模块，能够直接请求对应服务<br>

架构流程:

client                                                      tx                                                      service0                        service1 ...

准备分布式服务的所有分支事务数据
发起分布式事务请求------------------------------------>
                                                    解析请求并且初始化tx以及分支事务，对分支事务进行请求排序
                                                                                                ------------------>发起分支事务0请求
                                                                                                                    .....业务处理（请求获得锁，加锁，操作数据）
                                                                                                                    更新undo_log日志
                                                                                                <------------------返回响应
                                                    分支事务执行成功---->false:回滚之前所有分支事务（回滚事务仍然需要tx端向bt端发送回滚请求），并解锁
                                                                     >true:发起第二次分支事务-------------------------------------------------------------->....
                                                                                ....
                                                    所有分支事务处理成功，解锁
                                                    