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

	let employee = ''
	let shiftDates = []

function getPerson(name) {

    dom.person.innerHTML = name
    employee = name
    getShifts(name).then(function(value)
        {
            shiftDates = value
            renderCalendar(year, shiftDates)
            //console.log(shiftDates)
        })
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
	monthContentHTML.push(buildDates(year, monthNumber, month.days))
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

function isShift(year, month, day) {
    let date = new Date (year, month, day)
    for (let key in shiftDates) {
       let shift = new Date(shiftDates[key].split('-'));
       if (date.getTime() == shift.getTime()) {
            return true
       }
    }
    return false
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
		    let isInShift = isShift(year, month, day)
			if ((i + day) % 7 == 0 || (i + day) % 7 == 1) {
				dateHTML = buildDate(day, true, isInShift)
			} else {
				dateHTML = buildDate(day, false, isInShift)
			}

			day++
		}
		datesHTML.push(dateHTML)
	}
	return datesHTML.join('')
}

function buildDate(content, isAccent = false, isRound = false) {
	let cls = isAccent ? 'month_date month_date_accent' : 'month_date'
	if (isRound) {
	    cls = cls + ' round'
	}
	return `<div class="${cls}">${content}</div>`
}