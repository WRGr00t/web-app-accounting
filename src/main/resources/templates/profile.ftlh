<#import "parts/common.ftlh" as c>

<@c.page>
<h5>${user.username} (редактирование профиля)</h5>
<ul class="nav nav-tabs" id="myTab" role="tablist">
	<li class="nav-item" role="presentation">
		<a class="nav-link active" id="main-tab" data-toggle="tab" href="#main" role="tab" aria-controls="main" aria-selected="true">Профиль</a>
	</li>
	<li class="nav-item" role="presentation">
		<a class="nav-link" id="email-tab" data-toggle="tab" href="#email" role="tab" aria-controls="email" aria-selected="false">E-mail</a>
	</li>
	<li class="nav-item" role="presentation">
		<a class="nav-link" id="subscribe-tab" data-toggle="tab" href="#subscribe" role="tab" aria-controls="subscribe" aria-selected="false">Подписки</a>
	</li>
</ul>
<div class="tab-content" id="myTabContent">
	<div class="tab-pane fade show active" id="main" role="tabpanel" aria-labelledby="main">
		<form method="post">
			<div class="form-group row my-2">
				<label class="col-sm-2 col-form-label"> Password: </label>
				<div class="col-sm-6">
					<input type="password" name="password" class="form-control" placeholder="Пароль"/>
				</div>
				<input type="hidden" name="_csrf" value="${_csrf.token}"/>
			</div>
			<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Сохранить</button>
		</form>
	</div>
	<div class="tab-pane fade" id="email" role="tabpanel" aria-labelledby="email-tab">
			<form method="post">
				<div class="form-group row my-2">
					<label class="col-sm-2 col-form-label"> User email:</label>
					<div class="col-sm-6">
						<#if mail??>
							<input type="text" name="email" class="form-control" value="${mail}"/>
						<#else>
							<input type="text" name="email" class="form-control" value="Email не установлен"/>
						</#if>
					</div>
					<input type="hidden" name="_csrf" value="${_csrf.token}"/>
				</div>
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Сохранить</button>
			</form>
	</div>
	<div class="tab-pane fade" id="subscribe" role="tabpanel" aria-labelledby="subscribe-tab">
		<form method="post">
		<div class="form-group row my-2">
			<#if !message2??>
				<div class="col-sm-6">
				    <#if !message1??>
					    <h4>Список подписок</h4>
					    <table class="table table-striped">
						    <thead>
							    <tr>
								    <th scope="col">Пользователь</th>
								    <th scope="col">email</th>
								    <th scope="col">Операции</th>
							    </tr>
						    </thead>
						    <tbody>
							    <#list subscribes as subscribe>
							    <tr scope="row">
								    <td>${subscribe.username}</td>
								    <td>${subscribe.email}</td>
								    <form action="/subscribe/${subscribe.id?number}/delete" method="post">
									    <td>
										    <button class="btn btn-outline-danger my-2 my-sm-0" type="submit"
											    <#if subpage??>
												    disabled
											    </#if>>
											    Удалить
										    </button>
									    </td>
									    <input type="hidden" name="_csrf" value="${_csrf.token}">
								    </form>
							    </tr>
							    </#list>
							    <tr scope="row">
								    <td colspan="2">
									    Для удаления подписки и добавления более одного адреса обратитесь к администратору
								    </td>
							    </tr>
						    </tbody>
					    </table>
					<#else>
                    	<div class="col-sm-6">
                    	    <h4>${message1}</h4>
                    	    <table class="table table-striped">
                            	<tr scope="row">
                            		<form action="/subscribe" method="post">
                            	        <td>
                            				<select id="nameForSubscribe" name="nameForSubscribe">
                            					<#list persons as person>
                            						<option value="${person}"
                            							<#if person == select>
                            								selected
                            							</#if>>
                            								${person}
                            						</option>
                            					</#list>
                            				</select>
                            			</td>
                            		    <td>
                            				<input type="email" name="email" placeholder="${mail}" disabled>
                            				<input type="hidden" name="_csrf" value="${_csrf.token}">
                            			</td>
                            			<td>
                            				<button class="btn btn-outline-success my-2 my-sm-0" type="submit"
                            					<#if mail="Не установлен">
                            						disabled
                            					</#if>>
                            						Добавить
                            				</button>
                            			</td>
                            		</form>
                            	</tr>
                            </table>
                    	</div>
                    </#if>
				</div>
			</#if>
		</form>
	</div>
</div>
</@c.page>