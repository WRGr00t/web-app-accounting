<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<#include "parts/security.ftlh">
<@c.page>
    <p class="h4">Привет, ${name}!</p>
    <div>
        <span>Это страница сервиса уточнения времени работы сотрудников на сменах, анализа наполненности смены и учета нагрузки сотрудников.</span><br>
        <#if !known>
            <b>Для продолжения работы необходимо авторизоваться.</b>
        </#if>
        <#if known>
            <a href="/calendar">Календарь</a>
            <iframe frameborder="0" style="border:none;width:100%;height:450px;" width="100%" height="450" src="https://music.yandex.ru/iframe/#playlist/music-blog/1101">Слушайте <a href='https://music.yandex.ru/users/music-blog/playlists/1101'>100 суперхитов</a> — <a href='https://music.yandex.ru/users/music-blog'>Яндекс Музыка</a> на Яндекс Музыке</iframe>
        </#if>
    </div>

</@c.page>