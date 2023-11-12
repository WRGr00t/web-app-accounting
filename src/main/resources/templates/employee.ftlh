<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<@c.page>
<h4>Список подписок</h4>
${message?ifExists}
<table class="table table-striped">
    <thead>
        <tr>
            <th scope="col">Пользователь</th>
            <th scope="col">email</th>
            <th scope="col">Операции</th>
       </tr>
   </thead>
    <tbody>
        <#list employees as subscribe>
        <tr scope="row">
                <td>${subscribe.username}</td>
                <td>${subscribe.email}</td>
                <form action="/subscribe/${subscribe.id?number}/delete" method="post">
                    <td><button class="btn btn-outline-danger my-2 my-sm-0" type="submit">Удалить</button></td>
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                </form>
        </tr>
        </#list>
        <tr scope="row">
        <form action="/subscribe" method="post">
            <td>
                <select id="person" name="person">
                     <#list persons as person>
                          <option value="${person}" <#if person == select>selected</#if>>
                               ${person}
                          </option>
                     </#list>
                </select>
            </td>
            <td>
                <input type="email" name="email" placeholder="email@domain.ru">
                <input type="hidden" name="_csrf" value="${_csrf.token}">
            </td>
            <td>
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Добавить</button>
            </td>
        </form>
        </tr>
    </tbody>
</table>
</@c.page>