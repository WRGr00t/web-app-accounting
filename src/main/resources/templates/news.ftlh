<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<@c.page>
<table class="table table-striped">
      <thead>
            <tr>
                <th scope="col">История изменений</th>
           </tr>
       </thead>
    <#list messages as message>
        <tr scope="row">
            <td>${message}</td>
        </tr>
    <#else>
    Нет данных для отображения
    </#list>
    </table>
</@c.page>