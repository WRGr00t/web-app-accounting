<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<@c.page>
<div>
    <@l.logout />
</div>
<div>
List of users
</div>
<table border="1">
    <thead>
        <tr>
            <th>Name</th>
            <th>Role</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <#list users as user>
        <tr>
            <td>${user.username}</td>
            <td><#list user.roles as role>${role}<#sep>, </#list></td>
            <td><a href="/user/${user.id}">edit</a></td>
        </tr>
        </#list>
    </tbody>
</table>
</@c.page>