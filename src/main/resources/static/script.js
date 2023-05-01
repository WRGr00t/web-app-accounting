const dom = {
	calendar: document.getElementById('calendar'),
	year: document.getElementById('year'),
	person: document.getElementById('person')
}

const months = [
		{name: 'Январь'},
		{name: 'Февраль'},
		{name: 'Март'},
		{name: 'Апрель'},
		{name: 'Май'},
		{name: 'Июнь'},
		{name: 'Июль'},
		{name: 'Август'},
		{name: 'Сентябрь'},
		{name: 'Октябрь'},
		{name: 'Ноябрь'},
		{name: 'Декабрь'},
	]

	//let employee = ''
	let shiftDates = []
	let shiftObj = []

function getPerson(name) {

    dom.person.innerHTML = name
    //employee = name

    getShiftObjs(name).then(function(value)
        {
            shiftObj = value
        /*})

    getShifts(name).then(function(value)
        {
            shiftDates = value*/
            renderCalendar(year)
        })
    }

function getShiftObjs(name) {
    let requestURL = "/api/bynameandmonth?name=" + name + "&year=" + year
    return fetch(requestURL)
        .then((result) => result.json())
}

function getShifts(name) {
    let requestURL = "/api/byname?name=" + name + "&year=" + year
    return fetch(requestURL)
    .then((result) => result.json())
}

const year = new Date().getFullYear()
dom.year.innerHTML = year

function renderCalendar(year) {
//удаление календаря предыдущего сотрудника
    while (dom.calendar.firstChild) {
        dom.calendar.removeChild(dom.calendar.firstChild);
    }
//отрисовка нового календаря
	for (let i = 0; i < 12; i++) {
	buildMonth(i, year)
	}
}

function buildMonth(monthNumber, year) {
	const month = months[monthNumber]
	const monthHeadString = buildMonthHeadHTML(month.name)

	const monthBox = document.createElement('div')
	monthBox.className = 'month'
	const monthContentHTML = []

	monthContentHTML.push(buildMonthHeadHTML(month.name))
	monthContentHTML.push('<div class="month_content">')
	monthContentHTML.push(buildWeekDaysNames())
	monthContentHTML.push(buildDates(year, monthNumber))
	monthContentHTML.push('</div>')

	monthBox.innerHTML = monthContentHTML.join('')

	dom.calendar.appendChild(monthBox)
}

function buildMonthHeadHTML(monthName) {
	return `
		<div class="month_name">${monthName}</div>
	`
}

function buildWeekDaysNames() {
	const weekDayNames = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ', 'ВС']
	const daysNames = []
	for (let i = 0; i < 7; i++) {
		let dayNameTag
		if (i < 5) {
			dayNameTag = `<div class="month_date month">${weekDayNames[i]}</div>`
		} else {
			dayNameTag = `<div class="month_date month_date_accent">${weekDayNames[i]}</div>`
		}
		daysNames.push(dayNameTag)
	}
	return daysNames.join('')
}

/*function isShift(year, month, day) {
    let date = new Date (year, month, day)
    for (let key in shiftDates) {
       let shift = new Date(shiftDates[key].split('-'));
       if (date.getTime() == shift.getTime()) {
            return true
       }
    }
    return false
}*/
function isShift(year, month, day) {
    month = month + 1
        if(month < 10) {
            month = '0' + month
        }
        if(day < 10) {
            day = '0' + day
        }
        let dateText = year + '-' + month + '-' + day
        let isShiftText = false

        for (let key in shiftObj) {
            let d = shiftObj[key].date
            if (d === dateText){
                isShiftText = true
            }
        }
    return isShiftText
}

function buildDates(year, month) {

	const date = new Date(year, month, 1)
	const datesHTML = []
	const weekDayStart = date.getDay()
	const daysCount = 33 - new Date(year, month, 33).getDate();
	let i = 1
	let day = 1
	while (day < daysCount + 1) {
		let dateHTML;
		if (weekDayStart > i || (weekDayStart == 0 && i < 7)) {
			dateHTML = buildDate('')
			i++
		}
		else {
		    let status = getStatus(year, month, day)
			if ((i + day) % 7 == 0 || (i + day) % 7 == 1) {
				dateHTML = buildDate(day, month, true, status)
			} else {
				dateHTML = buildDate(day, month, false, status)
			}
			day++
		}
		datesHTML.push(dateHTML)
	}
	return datesHTML.join('')
}

function buildDate(content, month, isAccent = false, status) {
	let cls = isAccent ? 'month_date month_date_accent' : 'month_date'
    let desc = ""
    switch (status) {
      case 'DAYSHIFT': {
        cls = cls + ' day';
        //let normMonth = normalizeMonth(month);
        //let normDay = normalizeDay(content);
        desc = getDescription(year, month, content);
        //console.log(normDay + "/" + normMonth + "/" + year + " " + desc);
        break;
      }
      case 'NIGHTSHIFT': {
        cls = cls + ' night';
        desc = getDescription(year, month, content);
        break;
      }
      case 'HOLIDAY': {
        cls = cls + ' holiday';
        break;
      }
      case 'SICKDAY': {
        cls = cls + ' sickday';
        break;
      }
    }
    let result = `<div class="${cls}">${content}</div>`
    if (desc != null && typeof str !== "undefined") {
       desc = desc.trim();
    }
    if(desc) {
        result = `<div class="${cls}"data-tooltip="${desc}">${content}</div>`
    }
	return result
}

function getStatus(year, month, day) {

    day = normalizeDay(day)
    month = normalizeMonth(month)
    let dateText = year + '-' + month + '-' + day
    let status

    for (let key in shiftObj) {
        let d = shiftObj[key].date
        if (d === dateText){
            status = shiftObj[key].status
        }
    }

    return status
}

function getDescription(year, month, day) {
    day = normalizeDay(day)
    month = normalizeMonth(month)
    let dateText = year + '-' + month + '-' + day
    let description

    for (let key in shiftObj) {
       let d = shiftObj[key].date
       if (d === dateText){
          description = shiftObj[key].description
          //console.log(shiftObj[key].name + ' ' + shiftObj[key].date + ' ' + shiftObj[key].status)
       }
    }

    return description
}

function normalizeDay(day) {
    if(day < 10) {
      day = '0' + day
    }
    return day;
}

function normalizeMonth(month) {
    month = month + 1
    if(month < 10) {
      month = '0' + month
    }
    return month;
}

let tooltipElem;

    document.onmouseover = function(event) {
      let target = event.target;

      // если у нас есть подсказка...
      let tooltipHtml = target.dataset.tooltip;
      if (!tooltipHtml) return;

      // ...создадим элемент для подсказки

      tooltipElem = document.createElement('div');
      tooltipElem.className = 'tooltip';
      tooltipElem.innerHTML = tooltipHtml;
      document.body.append(tooltipElem);

      // спозиционируем его сверху от аннотируемого элемента (top-center)
      let coords = target.getBoundingClientRect();

      let left = coords.left + (target.offsetWidth - tooltipElem.offsetWidth) / 2;
      if (left < 0) left = 0; // не заезжать за левый край окна

      let top = coords.top - tooltipElem.offsetHeight - 5;
      if (top < 0) { // если подсказка не помещается сверху, то отображать её снизу
        top = coords.top + target.offsetHeight + 5;
      }

      tooltipElem.style.left = left + 'px';
      tooltipElem.style.top = top + 'px';
    };

    document.onmouseout = function(e) {

      if (tooltipElem) {
        tooltipElem.remove();
        tooltipElem = null;
      }
};