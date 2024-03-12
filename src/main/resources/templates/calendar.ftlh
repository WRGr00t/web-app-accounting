<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="stylesheet" type="text/css" href="static/style.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css" />
    <title>InShiftApp</title>
    <link rel="icon" href="static/img/favicon.ico" type="image/x-icon">

</head>
<body>
<div class="container-fluid">
    <div class="head">
        <div>
            <a href="/">Назад</a>
        </div>
        <div class="desc">
            <div id="person"></div>
            <i class="fas fa-angle-left prev"></i>
            <div id="year"></div>
            <i class="fas fa-angle-right next"></i>
        </div>
        <form>
            <div class="form-control">
                 <select id="person" name="person" onchange="getPerson(this.value)">
                       <#list persons as person>
                             <option value="${person}" <#if person == select>selected</#if>>
                                  ${person}
                             </option>
                       </#list>
                 </select>
                 <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            </div>
        </form>
    </div>

    <details>
        <summary class="legend_text">Легенда</summary>
        <table>
          <thead>
            <tr>
              <th>Описание</th>
              <th>Вид</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="legend_text">стандартная смена 9-21</td>
              <td class="month_date day">1</td>
            </tr>
            <tr>
              <td class="legend_text">облегченная смена меньше 12 часов</td>
              <td class="month_date light">2</td>
            </tr>
            <tr>
              <td class="legend_text">хардкор-смена больше 12 часов</td>
              <td class="month_date hard">3</td>
            </tr>
            <tr>
               <td class="legend_text">ночная смена</td>
               <td class="month_date night">4</td>
            </tr>
            <tr>
                <td class="legend_text">отпуск, отсутствие</td>
                <td class="month_date holiday">5</td>
            </tr>
            <tr>
                <td class="legend_text">больничный</td>
                <td class="month_date sickday">6</td>
            </tr>
            <tr>
                <td class="legend_text">увольнение</td>
                <td class="month_date dismissal">7</td>
            </tr>
            <tr>
                <td class="legend_text">командировка</td>
                <td class="month_date btrip">8</td>
            </tr>
          </tbody>
        </table>
    </details>

    <div id="calendar" class="calendar">

    </div>
    <script type="text/javascript" src="static/script.js"></script>
</div>
</body>
</html>