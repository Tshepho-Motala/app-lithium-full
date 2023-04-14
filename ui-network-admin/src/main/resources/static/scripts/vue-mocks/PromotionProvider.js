/******/ (function(modules) { // webpackBootstrap
    /******/ 	// The module cache
    /******/ 	var installedModules = {};
    /******/
    /******/ 	// The require function
    /******/ 	function __webpack_require__(moduleId) {
        /******/
        /******/ 		// Check if module is in cache
        /******/ 		if(installedModules[moduleId]) {
            /******/ 			return installedModules[moduleId].exports;
            /******/ 		}
        /******/ 		// Create a new module (and put it into the cache)
        /******/ 		var module = installedModules[moduleId] = {
            /******/ 			i: moduleId,
            /******/ 			l: false,
            /******/ 			exports: {}
            /******/ 		};
        /******/
        /******/ 		// Execute the module function
        /******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
        /******/
        /******/ 		// Flag the module as loaded
        /******/ 		module.l = true;
        /******/
        /******/ 		// Return the exports of the module
        /******/ 		return module.exports;
        /******/ 	}
    /******/
    /******/
    /******/ 	// expose the modules object (__webpack_modules__)
    /******/ 	__webpack_require__.m = modules;
    /******/
    /******/ 	// expose the module cache
    /******/ 	__webpack_require__.c = installedModules;
    /******/
    /******/ 	// define getter function for harmony exports
    /******/ 	__webpack_require__.d = function(exports, name, getter) {
        /******/ 		if(!__webpack_require__.o(exports, name)) {
            /******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
            /******/ 		}
        /******/ 	};
    /******/
    /******/ 	// define __esModule on exports
    /******/ 	__webpack_require__.r = function(exports) {
        /******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
            /******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
            /******/ 		}
        /******/ 		Object.defineProperty(exports, '__esModule', { value: true });
        /******/ 	};
    /******/
    /******/ 	// create a fake namespace object
    /******/ 	// mode & 1: value is a module id, require it
    /******/ 	// mode & 2: merge all properties of value into the ns
    /******/ 	// mode & 4: return value when already ns object
    /******/ 	// mode & 8|1: behave like require
    /******/ 	__webpack_require__.t = function(value, mode) {
        /******/ 		if(mode & 1) value = __webpack_require__(value);
        /******/ 		if(mode & 8) return value;
        /******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
        /******/ 		var ns = Object.create(null);
        /******/ 		__webpack_require__.r(ns);
        /******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
        /******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
        /******/ 		return ns;
        /******/ 	};
    /******/
    /******/ 	// getDefaultExport function for compatibility with non-harmony modules
    /******/ 	__webpack_require__.n = function(module) {
        /******/ 		var getter = module && module.__esModule ?
            /******/ 			function getDefault() { return module['default']; } :
            /******/ 			function getModuleExports() { return module; };
        /******/ 		__webpack_require__.d(getter, 'a', getter);
        /******/ 		return getter;
        /******/ 	};
    /******/
    /******/ 	// Object.prototype.hasOwnProperty.call
    /******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
    /******/
    /******/ 	// __webpack_public_path__
    /******/ 	__webpack_require__.p = "";
    /******/
    /******/
    /******/ 	// Load entry module and return exports
    /******/ 	return __webpack_require__(__webpack_require__.s = "./src/mock/provider/PromotionProviderMock.ts");
    /******/ })
    /************************************************************************/
    /******/ ({

        /***/ "./node_modules/nanoid/index.browser.js":
        /*!**********************************************!*\
  !*** ./node_modules/nanoid/index.browser.js ***!
  \**********************************************/
        /*! exports provided: urlAlphabet, random, customRandom, customAlphabet, nanoid */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "random", function() { return random; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "customRandom", function() { return customRandom; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "customAlphabet", function() { return customAlphabet; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "nanoid", function() { return nanoid; });
            /* harmony import */ var _url_alphabet_index_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./url-alphabet/index.js */ "./node_modules/nanoid/url-alphabet/index.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "urlAlphabet", function() { return _url_alphabet_index_js__WEBPACK_IMPORTED_MODULE_0__["urlAlphabet"]; });


            let random = bytes => crypto.getRandomValues(new Uint8Array(bytes))
            let customRandom = (alphabet, defaultSize, getRandom) => {
                let mask = (2 << (Math.log(alphabet.length - 1) / Math.LN2)) - 1
                let step = -~((1.6 * mask * defaultSize) / alphabet.length)
                return (size = defaultSize) => {
                    let id = ''
                    while (true) {
                        let bytes = getRandom(step)
                        let j = step
                        while (j--) {
                            id += alphabet[bytes[j] & mask] || ''
                            if (id.length === size) return id
                        }
                    }
                }
            }
            let customAlphabet = (alphabet, size = 21) =>
                customRandom(alphabet, size, random)
            let nanoid = (size = 21) =>
                crypto.getRandomValues(new Uint8Array(size)).reduce((id, byte) => {
                    byte &= 63
                    if (byte < 36) {
                        id += byte.toString(36)
                    } else if (byte < 62) {
                        id += (byte - 26).toString(36).toUpperCase()
                    } else if (byte > 62) {
                        id += '-'
                    } else {
                        id += '_'
                    }
                    return id
                }, '')


            /***/ }),

        /***/ "./node_modules/nanoid/url-alphabet/index.js":
        /*!***************************************************!*\
  !*** ./node_modules/nanoid/url-alphabet/index.js ***!
  \***************************************************/
        /*! exports provided: urlAlphabet */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "urlAlphabet", function() { return urlAlphabet; });
            const urlAlphabet =
                'useandom-26T198340PX75pxJACKVERYMINDBUSHWOLF_GQZbfghjklqvwyzrict'


            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/cache.js":
        /*!**********************************************!*\
  !*** ./node_modules/rrule/dist/esm/cache.js ***!
  \**********************************************/
        /*! exports provided: Cache */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Cache", function() { return Cache; });
            /* harmony import */ var _iterresult__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./iterresult */ "./node_modules/rrule/dist/esm/iterresult.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");



            function argsMatch(left, right) {
                if (Array.isArray(left)) {
                    if (!Array.isArray(right))
                        return false;
                    if (left.length !== right.length)
                        return false;
                    return left.every(function (date, i) { return date.getTime() === right[i].getTime(); });
                }
                if (left instanceof Date) {
                    return right instanceof Date && left.getTime() === right.getTime();
                }
                return left === right;
            }
            var Cache = /** @class */ (function () {
                function Cache() {
                    this.all = false;
                    this.before = [];
                    this.after = [];
                    this.between = [];
                }
                /**
                 * @param {String} what - all/before/after/between
                 * @param {Array,Date} value - an array of dates, one date, or null
                 * @param {Object?} args - _iter arguments
                 */
                Cache.prototype._cacheAdd = function (what, value, args) {
                    if (value) {
                        value =
                            value instanceof Date
                                ? _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].clone(value)
                                : _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].cloneDates(value);
                    }
                    if (what === 'all') {
                        this.all = value;
                    }
                    else {
                        args._value = value;
                        this[what].push(args);
                    }
                };
                /**
                 * @return false - not in the cache
                 * @return null  - cached, but zero occurrences (before/after)
                 * @return Date  - cached (before/after)
                 * @return []    - cached, but zero occurrences (all/between)
                 * @return [Date1, DateN] - cached (all/between)
                 */
                Cache.prototype._cacheGet = function (what, args) {
                    var cached = false;
                    var argsKeys = args ? Object.keys(args) : [];
                    var findCacheDiff = function (item) {
                        for (var i = 0; i < argsKeys.length; i++) {
                            var key = argsKeys[i];
                            if (!argsMatch(args[key], item[key])) {
                                return true;
                            }
                        }
                        return false;
                    };
                    var cachedObject = this[what];
                    if (what === 'all') {
                        cached = this.all;
                    }
                    else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(cachedObject)) {
                        // Let's see whether we've already called the
                        // 'what' method with the same 'args'
                        for (var i = 0; i < cachedObject.length; i++) {
                            var item = cachedObject[i];
                            if (argsKeys.length && findCacheDiff(item))
                                continue;
                            cached = item._value;
                            break;
                        }
                    }
                    if (!cached && this.all) {
                        // Not in the cache, but we already know all the occurrences,
                        // so we can find the correct dates from the cached ones.
                        var iterResult = new _iterresult__WEBPACK_IMPORTED_MODULE_0__["default"](what, args);
                        for (var i = 0; i < this.all.length; i++) {
                            if (!iterResult.accept(this.all[i]))
                                break;
                        }
                        cached = iterResult.getValue();
                        this._cacheAdd(what, cached, args);
                    }
                    return Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(cached)
                        ? _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].cloneDates(cached)
                        : cached instanceof Date
                            ? _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].clone(cached)
                            : cached;
                };
                return Cache;
            }());

//# sourceMappingURL=cache.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/callbackiterresult.js":
        /*!***********************************************************!*\
  !*** ./node_modules/rrule/dist/esm/callbackiterresult.js ***!
  \***********************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _iterresult__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./iterresult */ "./node_modules/rrule/dist/esm/iterresult.js");


            /**
             * IterResult subclass that calls a callback function on each add,
             * and stops iterating when the callback returns false.
             */
            var CallbackIterResult = /** @class */ (function (_super) {
                Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"])(CallbackIterResult, _super);
                function CallbackIterResult(method, args, iterator) {
                    var _this = _super.call(this, method, args) || this;
                    _this.iterator = iterator;
                    return _this;
                }
                CallbackIterResult.prototype.add = function (date) {
                    if (this.iterator(date, this._result.length)) {
                        this._result.push(date);
                        return true;
                    }
                    return false;
                };
                return CallbackIterResult;
            }(_iterresult__WEBPACK_IMPORTED_MODULE_1__["default"]));
            /* harmony default export */ __webpack_exports__["default"] = (CallbackIterResult);
//# sourceMappingURL=callbackiterresult.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/datetime.js":
        /*!*************************************************!*\
  !*** ./node_modules/rrule/dist/esm/datetime.js ***!
  \*************************************************/
        /*! exports provided: Time, DateTime */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Time", function() { return Time; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DateTime", function() { return DateTime; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");




            var Time = /** @class */ (function () {
                function Time(hour, minute, second, millisecond) {
                    this.hour = hour;
                    this.minute = minute;
                    this.second = second;
                    this.millisecond = millisecond || 0;
                }
                Time.prototype.getHours = function () {
                    return this.hour;
                };
                Time.prototype.getMinutes = function () {
                    return this.minute;
                };
                Time.prototype.getSeconds = function () {
                    return this.second;
                };
                Time.prototype.getMilliseconds = function () {
                    return this.millisecond;
                };
                Time.prototype.getTime = function () {
                    return ((this.hour * 60 * 60 + this.minute * 60 + this.second) * 1000 +
                        this.millisecond);
                };
                return Time;
            }());

            var DateTime = /** @class */ (function (_super) {
                Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"])(DateTime, _super);
                function DateTime(year, month, day, hour, minute, second, millisecond) {
                    var _this = _super.call(this, hour, minute, second, millisecond) || this;
                    _this.year = year;
                    _this.month = month;
                    _this.day = day;
                    return _this;
                }
                DateTime.fromDate = function (date) {
                    return new this(date.getUTCFullYear(), date.getUTCMonth() + 1, date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds(), date.valueOf() % 1000);
                };
                DateTime.prototype.getWeekday = function () {
                    return _dateutil__WEBPACK_IMPORTED_MODULE_3__["dateutil"].getWeekday(new Date(this.getTime()));
                };
                DateTime.prototype.getTime = function () {
                    return new Date(Date.UTC(this.year, this.month - 1, this.day, this.hour, this.minute, this.second, this.millisecond)).getTime();
                };
                DateTime.prototype.getDay = function () {
                    return this.day;
                };
                DateTime.prototype.getMonth = function () {
                    return this.month;
                };
                DateTime.prototype.getYear = function () {
                    return this.year;
                };
                DateTime.prototype.addYears = function (years) {
                    this.year += years;
                };
                DateTime.prototype.addMonths = function (months) {
                    this.month += months;
                    if (this.month > 12) {
                        var yearDiv = Math.floor(this.month / 12);
                        var monthMod = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(this.month, 12);
                        this.month = monthMod;
                        this.year += yearDiv;
                        if (this.month === 0) {
                            this.month = 12;
                            --this.year;
                        }
                    }
                };
                DateTime.prototype.addWeekly = function (days, wkst) {
                    if (wkst > this.getWeekday()) {
                        this.day += -(this.getWeekday() + 1 + (6 - wkst)) + days * 7;
                    }
                    else {
                        this.day += -(this.getWeekday() - wkst) + days * 7;
                    }
                    this.fixDay();
                };
                DateTime.prototype.addDaily = function (days) {
                    this.day += days;
                    this.fixDay();
                };
                DateTime.prototype.addHours = function (hours, filtered, byhour) {
                    if (filtered) {
                        // Jump to one iteration before next day
                        this.hour += Math.floor((23 - this.hour) / hours) * hours;
                    }
                    for (;;) {
                        this.hour += hours;
                        var _a = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["divmod"])(this.hour, 24), dayDiv = _a.div, hourMod = _a.mod;
                        if (dayDiv) {
                            this.hour = hourMod;
                            this.addDaily(dayDiv);
                        }
                        if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(byhour) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(byhour, this.hour))
                            break;
                    }
                };
                DateTime.prototype.addMinutes = function (minutes, filtered, byhour, byminute) {
                    if (filtered) {
                        // Jump to one iteration before next day
                        this.minute +=
                            Math.floor((1439 - (this.hour * 60 + this.minute)) / minutes) * minutes;
                    }
                    for (;;) {
                        this.minute += minutes;
                        var _a = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["divmod"])(this.minute, 60), hourDiv = _a.div, minuteMod = _a.mod;
                        if (hourDiv) {
                            this.minute = minuteMod;
                            this.addHours(hourDiv, false, byhour);
                        }
                        if ((Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(byhour) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(byhour, this.hour)) &&
                            (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(byminute) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(byminute, this.minute))) {
                            break;
                        }
                    }
                };
                DateTime.prototype.addSeconds = function (seconds, filtered, byhour, byminute, bysecond) {
                    if (filtered) {
                        // Jump to one iteration before next day
                        this.second +=
                            Math.floor((86399 - (this.hour * 3600 + this.minute * 60 + this.second)) /
                                seconds) * seconds;
                    }
                    for (;;) {
                        this.second += seconds;
                        var _a = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["divmod"])(this.second, 60), minuteDiv = _a.div, secondMod = _a.mod;
                        if (minuteDiv) {
                            this.second = secondMod;
                            this.addMinutes(minuteDiv, false, byhour, byminute);
                        }
                        if ((Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(byhour) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(byhour, this.hour)) &&
                            (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(byminute) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(byminute, this.minute)) &&
                            (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(bysecond) || Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(bysecond, this.second))) {
                            break;
                        }
                    }
                };
                DateTime.prototype.fixDay = function () {
                    if (this.day <= 28) {
                        return;
                    }
                    var daysinmonth = _dateutil__WEBPACK_IMPORTED_MODULE_3__["dateutil"].monthRange(this.year, this.month - 1)[1];
                    if (this.day <= daysinmonth) {
                        return;
                    }
                    while (this.day > daysinmonth) {
                        this.day -= daysinmonth;
                        ++this.month;
                        if (this.month === 13) {
                            this.month = 1;
                            ++this.year;
                            if (this.year > _dateutil__WEBPACK_IMPORTED_MODULE_3__["dateutil"].MAXYEAR) {
                                return;
                            }
                        }
                        daysinmonth = _dateutil__WEBPACK_IMPORTED_MODULE_3__["dateutil"].monthRange(this.year, this.month - 1)[1];
                    }
                };
                DateTime.prototype.add = function (options, filtered) {
                    var freq = options.freq, interval = options.interval, wkst = options.wkst, byhour = options.byhour, byminute = options.byminute, bysecond = options.bysecond;
                    switch (freq) {
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].YEARLY:
                            return this.addYears(interval);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].MONTHLY:
                            return this.addMonths(interval);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].WEEKLY:
                            return this.addWeekly(interval, wkst);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].DAILY:
                            return this.addDaily(interval);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].HOURLY:
                            return this.addHours(interval, filtered, byhour);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].MINUTELY:
                            return this.addMinutes(interval, filtered, byhour, byminute);
                        case _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"].SECONDLY:
                            return this.addSeconds(interval, filtered, byhour, byminute, bysecond);
                    }
                };
                return DateTime;
            }(Time));

//# sourceMappingURL=datetime.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/dateutil.js":
        /*!*************************************************!*\
  !*** ./node_modules/rrule/dist/esm/dateutil.js ***!
  \*************************************************/
        /*! exports provided: dateutil, default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "dateutil", function() { return dateutil; });
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* eslint-disable @typescript-eslint/no-namespace */

            /**
             * General date-related utilities.
             * Also handles several incompatibilities between JavaScript and Python
             *
             */
            var dateutil;
            (function (dateutil) {
                dateutil.MONTH_DAYS = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                /**
                 * Number of milliseconds of one day
                 */
                dateutil.ONE_DAY = 1000 * 60 * 60 * 24;
                /**
                 * @see: <http://docs.python.org/library/datetime.html#datetime.MAXYEAR>
                 */
                dateutil.MAXYEAR = 9999;
                /**
                 * Python uses 1-Jan-1 as the base for calculating ordinals but we don't
                 * want to confuse the JS engine with milliseconds > Number.MAX_NUMBER,
                 * therefore we use 1-Jan-1970 instead
                 */
                dateutil.ORDINAL_BASE = new Date(Date.UTC(1970, 0, 1));
                /**
                 * Python: MO-SU: 0 - 6
                 * JS: SU-SAT 0 - 6
                 */
                dateutil.PY_WEEKDAYS = [6, 0, 1, 2, 3, 4, 5];
                /**
                 * py_date.timetuple()[7]
                 */
                dateutil.getYearDay = function (date) {
                    var dateNoTime = new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate());
                    return (Math.ceil((dateNoTime.valueOf() -
                            new Date(date.getUTCFullYear(), 0, 1).valueOf()) /
                        dateutil.ONE_DAY) + 1);
                };
                dateutil.isLeapYear = function (year) {
                    return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
                };
                dateutil.isDate = function (value) {
                    return value instanceof Date;
                };
                dateutil.isValidDate = function (value) {
                    return dateutil.isDate(value) && !isNaN(value.getTime());
                };
                /**
                 * @return {Number} the date's timezone offset in ms
                 */
                dateutil.tzOffset = function (date) {
                    return date.getTimezoneOffset() * 60 * 1000;
                };
                /**
                 * @see: <http://www.mcfedries.com/JavaScript/DaysBetween.asp>
                 */
                dateutil.daysBetween = function (date1, date2) {
                    // The number of milliseconds in one day
                    // Convert both dates to milliseconds
                    var date1ms = date1.getTime() - dateutil.tzOffset(date1);
                    var date2ms = date2.getTime() - dateutil.tzOffset(date2);
                    // Calculate the difference in milliseconds
                    var differencems = date1ms - date2ms;
                    // Convert back to days and return
                    return Math.round(differencems / dateutil.ONE_DAY);
                };
                /**
                 * @see: <http://docs.python.org/library/datetime.html#datetime.date.toordinal>
                 */
                dateutil.toOrdinal = function (date) {
                    return dateutil.daysBetween(date, dateutil.ORDINAL_BASE);
                };
                /**
                 * @see - <http://docs.python.org/library/datetime.html#datetime.date.fromordinal>
                 */
                dateutil.fromOrdinal = function (ordinal) {
                    return new Date(dateutil.ORDINAL_BASE.getTime() + ordinal * dateutil.ONE_DAY);
                };
                dateutil.getMonthDays = function (date) {
                    var month = date.getUTCMonth();
                    return month === 1 && dateutil.isLeapYear(date.getUTCFullYear())
                        ? 29
                        : dateutil.MONTH_DAYS[month];
                };
                /**
                 * @return {Number} python-like weekday
                 */
                dateutil.getWeekday = function (date) {
                    return dateutil.PY_WEEKDAYS[date.getUTCDay()];
                };
                /**
                 * @see: <http://docs.python.org/library/calendar.html#calendar.monthrange>
                 */
                dateutil.monthRange = function (year, month) {
                    var date = new Date(Date.UTC(year, month, 1));
                    return [dateutil.getWeekday(date), dateutil.getMonthDays(date)];
                };
                /**
                 * @see: <http://docs.python.org/library/datetime.html#datetime.datetime.combine>
                 */
                dateutil.combine = function (date, time) {
                    time = time || date;
                    return new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), time.getHours(), time.getMinutes(), time.getSeconds(), time.getMilliseconds()));
                };
                dateutil.clone = function (date) {
                    var dolly = new Date(date.getTime());
                    return dolly;
                };
                dateutil.cloneDates = function (dates) {
                    var clones = [];
                    for (var i = 0; i < dates.length; i++) {
                        clones.push(dateutil.clone(dates[i]));
                    }
                    return clones;
                };
                /**
                 * Sorts an array of Date or dateutil.Time objects
                 */
                dateutil.sort = function (dates) {
                    dates.sort(function (a, b) {
                        return a.getTime() - b.getTime();
                    });
                };
                dateutil.timeToUntilString = function (time, utc) {
                    if (utc === void 0) { utc = true; }
                    var date = new Date(time);
                    return [
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCFullYear().toString(), 4, '0'),
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCMonth() + 1, 2, '0'),
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCDate(), 2, '0'),
                        'T',
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCHours(), 2, '0'),
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCMinutes(), 2, '0'),
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["padStart"])(date.getUTCSeconds(), 2, '0'),
                        utc ? 'Z' : '',
                    ].join('');
                };
                dateutil.untilStringToDate = function (until) {
                    var re = /^(\d{4})(\d{2})(\d{2})(T(\d{2})(\d{2})(\d{2})Z?)?$/;
                    var bits = re.exec(until);
                    if (!bits)
                        throw new Error("Invalid UNTIL value: ".concat(until));
                    return new Date(Date.UTC(parseInt(bits[1], 10), parseInt(bits[2], 10) - 1, parseInt(bits[3], 10), parseInt(bits[5], 10) || 0, parseInt(bits[6], 10) || 0, parseInt(bits[7], 10) || 0));
                };
            })(dateutil || (dateutil = {}));
            /* harmony default export */ __webpack_exports__["default"] = (dateutil);
//# sourceMappingURL=dateutil.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/datewithzone.js":
        /*!*****************************************************!*\
  !*** ./node_modules/rrule/dist/esm/datewithzone.js ***!
  \*****************************************************/
        /*! exports provided: DateWithZone */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DateWithZone", function() { return DateWithZone; });
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");

            var DateWithZone = /** @class */ (function () {
                function DateWithZone(date, tzid) {
                    if (isNaN(date.getTime())) {
                        throw new RangeError('Invalid date passed to DateWithZone');
                    }
                    this.date = date;
                    this.tzid = tzid;
                }
                Object.defineProperty(DateWithZone.prototype, "isUTC", {
                    get: function () {
                        return !this.tzid || this.tzid.toUpperCase() === 'UTC';
                    },
                    enumerable: false,
                    configurable: true
                });
                DateWithZone.prototype.toString = function () {
                    var datestr = _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].timeToUntilString(this.date.getTime(), this.isUTC);
                    if (!this.isUTC) {
                        return ";TZID=".concat(this.tzid, ":").concat(datestr);
                    }
                    return ":".concat(datestr);
                };
                DateWithZone.prototype.getTime = function () {
                    return this.date.getTime();
                };
                DateWithZone.prototype.rezonedDate = function () {
                    var _a;
                    if (this.isUTC) {
                        return this.date;
                    }
                    var localTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
                    var dateInLocalTZ = new Date(this.date.toLocaleString(undefined, { timeZone: localTimeZone }));
                    var dateInTargetTZ = new Date(this.date.toLocaleString(undefined, { timeZone: (_a = this.tzid) !== null && _a !== void 0 ? _a : 'UTC' }));
                    var tzOffset = dateInTargetTZ.getTime() - dateInLocalTZ.getTime();
                    return new Date(this.date.getTime() - tzOffset);
                };
                return DateWithZone;
            }());

//# sourceMappingURL=datewithzone.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/helpers.js":
        /*!************************************************!*\
  !*** ./node_modules/rrule/dist/esm/helpers.js ***!
  \************************************************/
        /*! exports provided: isPresent, isNumber, isWeekdayStr, isArray, range, clone, repeat, toArray, padStart, split, pymod, divmod, empty, notEmpty, includes */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isPresent", function() { return isPresent; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isNumber", function() { return isNumber; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isWeekdayStr", function() { return isWeekdayStr; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isArray", function() { return isArray; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "range", function() { return range; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "clone", function() { return clone; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "repeat", function() { return repeat; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "toArray", function() { return toArray; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "padStart", function() { return padStart; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "split", function() { return split; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pymod", function() { return pymod; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "divmod", function() { return divmod; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "empty", function() { return empty; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "notEmpty", function() { return notEmpty; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "includes", function() { return includes; });
            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
// =============================================================================
// Helper functions
// =============================================================================

            var isPresent = function (value) {
                return value !== null && value !== undefined;
            };
            var isNumber = function (value) {
                return typeof value === 'number';
            };
            var isWeekdayStr = function (value) {
                return typeof value === 'string' && _weekday__WEBPACK_IMPORTED_MODULE_0__["ALL_WEEKDAYS"].includes(value);
            };
            var isArray = Array.isArray;
            /**
             * Simplified version of python's range()
             */
            var range = function (start, end) {
                if (end === void 0) { end = start; }
                if (arguments.length === 1) {
                    end = start;
                    start = 0;
                }
                var rang = [];
                for (var i = start; i < end; i++)
                    rang.push(i);
                return rang;
            };
            var clone = function (array) {
                return [].concat(array);
            };
            var repeat = function (value, times) {
                var i = 0;
                var array = [];
                if (isArray(value)) {
                    for (; i < times; i++)
                        array[i] = [].concat(value);
                }
                else {
                    for (; i < times; i++)
                        array[i] = value;
                }
                return array;
            };
            var toArray = function (item) {
                if (isArray(item)) {
                    return item;
                }
                return [item];
            };
            function padStart(item, targetLength, padString) {
                if (padString === void 0) { padString = ' '; }
                var str = String(item);
                targetLength = targetLength >> 0;
                if (str.length > targetLength) {
                    return String(str);
                }
                targetLength = targetLength - str.length;
                if (targetLength > padString.length) {
                    padString += repeat(padString, targetLength / padString.length);
                }
                return padString.slice(0, targetLength) + String(str);
            }
            /**
             * Python like split
             */
            var split = function (str, sep, num) {
                var splits = str.split(sep);
                return num
                    ? splits.slice(0, num).concat([splits.slice(num).join(sep)])
                    : splits;
            };
            /**
             * closure/goog/math/math.js:modulo
             * Copyright 2006 The Closure Library Authors.
             * The % operator in JavaScript returns the remainder of a / b, but differs from
             * some other languages in that the result will have the same sign as the
             * dividend. For example, -1 % 8 == -1, whereas in some other languages
             * (such as Python) the result would be 7. This function emulates the more
             * correct modulo behavior, which is useful for certain applications such as
             * calculating an offset index in a circular list.
             *
             * @param {number} a The dividend.
             * @param {number} b The divisor.
             * @return {number} a % b where the result is between 0 and b (either 0 <= x < b
             * or b < x <= 0, depending on the sign of b).
             */
            var pymod = function (a, b) {
                var r = a % b;
                // If r and b differ in sign, add b to wrap the result to the correct sign.
                return r * b < 0 ? r + b : r;
            };
            /**
             * @see: <http://docs.python.org/library/functions.html#divmod>
             */
            var divmod = function (a, b) {
                return { div: Math.floor(a / b), mod: pymod(a, b) };
            };
            var empty = function (obj) {
                return !isPresent(obj) || obj.length === 0;
            };
            /**
             * Python-like boolean
             *
             * @return {Boolean} value of an object/primitive, taking into account
             * the fact that in Python an empty list's/tuple's
             * boolean value is False, whereas in JS it's true
             */
            var notEmpty = function (obj) {
                return !empty(obj);
            };
            /**
             * Return true if a value is in an array
             */
            var includes = function (arr, val) {
                return notEmpty(arr) && arr.indexOf(val) !== -1;
            };
//# sourceMappingURL=helpers.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/index.js":
        /*!**********************************************!*\
  !*** ./node_modules/rrule/dist/esm/index.js ***!
  \**********************************************/
        /*! exports provided: RRule, RRuleSet, rrulestr, Frequency, Weekday */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RRule", function() { return _rrule__WEBPACK_IMPORTED_MODULE_0__["RRule"]; });

            /* harmony import */ var _rruleset__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./rruleset */ "./node_modules/rrule/dist/esm/rruleset.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RRuleSet", function() { return _rruleset__WEBPACK_IMPORTED_MODULE_1__["RRuleSet"]; });

            /* harmony import */ var _rrulestr__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./rrulestr */ "./node_modules/rrule/dist/esm/rrulestr.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "rrulestr", function() { return _rrulestr__WEBPACK_IMPORTED_MODULE_2__["rrulestr"]; });

            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "Frequency", function() { return _types__WEBPACK_IMPORTED_MODULE_3__["Frequency"]; });

            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "Weekday", function() { return _weekday__WEBPACK_IMPORTED_MODULE_4__["Weekday"]; });

            /* !
 * rrule.js - Library for working with recurrence rules for calendar dates.
 * https://github.com/jakubroztocil/rrule
 *
 * Copyright 2010, Jakub Roztocil and Lars Schoning
 * Licenced under the BSD licence.
 * https://github.com/jakubroztocil/rrule/blob/master/LICENCE
 *
 * Based on:
 * python-dateutil - Extensions to the standard Python datetime module.
 * Copyright (c) 2003-2011 - Gustavo Niemeyer <gustavo@niemeyer.net>
 * Copyright (c) 2012 - Tomi Pieviläinen <tomi.pievilainen@iki.fi>
 * https://github.com/jakubroztocil/rrule/blob/master/LICENCE
 *
 */





//# sourceMappingURL=index.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iter/index.js":
        /*!***************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iter/index.js ***!
  \***************************************************/
        /*! exports provided: iter */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "iter", function() { return iter; });
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _iterinfo_index__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../iterinfo/index */ "./node_modules/rrule/dist/esm/iterinfo/index.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _parseoptions__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../parseoptions */ "./node_modules/rrule/dist/esm/parseoptions.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _datewithzone__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../datewithzone */ "./node_modules/rrule/dist/esm/datewithzone.js");
            /* harmony import */ var _poslist__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./poslist */ "./node_modules/rrule/dist/esm/iter/poslist.js");
            /* harmony import */ var _datetime__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../datetime */ "./node_modules/rrule/dist/esm/datetime.js");









            function iter(iterResult, options) {
                var dtstart = options.dtstart, freq = options.freq, interval = options.interval, until = options.until, bysetpos = options.bysetpos;
                var count = options.count;
                if (count === 0 || interval === 0) {
                    return emitResult(iterResult);
                }
                var counterDate = _datetime__WEBPACK_IMPORTED_MODULE_8__["DateTime"].fromDate(dtstart);
                var ii = new _iterinfo_index__WEBPACK_IMPORTED_MODULE_2__["default"](options);
                ii.rebuild(counterDate.year, counterDate.month);
                var timeset = makeTimeset(ii, counterDate, options);
                for (;;) {
                    var _a = ii.getdayset(freq)(counterDate.year, counterDate.month, counterDate.day), dayset = _a[0], start = _a[1], end = _a[2];
                    var filtered = removeFilteredDays(dayset, start, end, ii, options);
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(bysetpos)) {
                        var poslist = Object(_poslist__WEBPACK_IMPORTED_MODULE_7__["buildPoslist"])(bysetpos, timeset, start, end, ii, dayset);
                        for (var j = 0; j < poslist.length; j++) {
                            var res = poslist[j];
                            if (until && res > until) {
                                return emitResult(iterResult);
                            }
                            if (res >= dtstart) {
                                var rezonedDate = rezoneIfNeeded(res, options);
                                if (!iterResult.accept(rezonedDate)) {
                                    return emitResult(iterResult);
                                }
                                if (count) {
                                    --count;
                                    if (!count) {
                                        return emitResult(iterResult);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        for (var j = start; j < end; j++) {
                            var currentDay = dayset[j];
                            if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["isPresent"])(currentDay)) {
                                continue;
                            }
                            var date = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].fromOrdinal(ii.yearordinal + currentDay);
                            for (var k = 0; k < timeset.length; k++) {
                                var time = timeset[k];
                                var res = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].combine(date, time);
                                if (until && res > until) {
                                    return emitResult(iterResult);
                                }
                                if (res >= dtstart) {
                                    var rezonedDate = rezoneIfNeeded(res, options);
                                    if (!iterResult.accept(rezonedDate)) {
                                        return emitResult(iterResult);
                                    }
                                    if (count) {
                                        --count;
                                        if (!count) {
                                            return emitResult(iterResult);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (options.interval === 0) {
                        return emitResult(iterResult);
                    }
                    // Handle frequency and interval
                    counterDate.add(options, filtered);
                    if (counterDate.year > _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].MAXYEAR) {
                        return emitResult(iterResult);
                    }
                    if (!Object(_types__WEBPACK_IMPORTED_MODULE_0__["freqIsDailyOrGreater"])(freq)) {
                        timeset = ii.gettimeset(freq)(counterDate.hour, counterDate.minute, counterDate.second, 0);
                    }
                    ii.rebuild(counterDate.year, counterDate.month);
                }
            }
            function isFiltered(ii, currentDay, options) {
                var bymonth = options.bymonth, byweekno = options.byweekno, byweekday = options.byweekday, byeaster = options.byeaster, bymonthday = options.bymonthday, bynmonthday = options.bynmonthday, byyearday = options.byyearday;
                return ((Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(bymonth) && !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(bymonth, ii.mmask[currentDay])) ||
                    (Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(byweekno) && !ii.wnomask[currentDay]) ||
                    (Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(byweekday) && !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byweekday, ii.wdaymask[currentDay])) ||
                    (Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(ii.nwdaymask) && !ii.nwdaymask[currentDay]) ||
                    (byeaster !== null && !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(ii.eastermask, currentDay)) ||
                    ((Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(bymonthday) || Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(bynmonthday)) &&
                        !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(bymonthday, ii.mdaymask[currentDay]) &&
                        !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(bynmonthday, ii.nmdaymask[currentDay])) ||
                    (Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(byyearday) &&
                        ((currentDay < ii.yearlen &&
                                !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byyearday, currentDay + 1) &&
                                !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byyearday, -ii.yearlen + currentDay)) ||
                            (currentDay >= ii.yearlen &&
                                !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byyearday, currentDay + 1 - ii.yearlen) &&
                                !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byyearday, -ii.nextyearlen + currentDay - ii.yearlen)))));
            }
            function rezoneIfNeeded(date, options) {
                return new _datewithzone__WEBPACK_IMPORTED_MODULE_6__["DateWithZone"](date, options.tzid).rezonedDate();
            }
            function emitResult(iterResult) {
                return iterResult.getValue();
            }
            function removeFilteredDays(dayset, start, end, ii, options) {
                var filtered = false;
                for (var dayCounter = start; dayCounter < end; dayCounter++) {
                    var currentDay = dayset[dayCounter];
                    filtered = isFiltered(ii, currentDay, options);
                    if (filtered)
                        dayset[currentDay] = null;
                }
                return filtered;
            }
            function makeTimeset(ii, counterDate, options) {
                var freq = options.freq, byhour = options.byhour, byminute = options.byminute, bysecond = options.bysecond;
                if (Object(_types__WEBPACK_IMPORTED_MODULE_0__["freqIsDailyOrGreater"])(freq)) {
                    return Object(_parseoptions__WEBPACK_IMPORTED_MODULE_4__["buildTimeset"])(options);
                }
                if ((freq >= _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].HOURLY &&
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(byhour) &&
                        !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byhour, counterDate.hour)) ||
                    (freq >= _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MINUTELY &&
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(byminute) &&
                        !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(byminute, counterDate.minute)) ||
                    (freq >= _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].SECONDLY &&
                        Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["notEmpty"])(bysecond) &&
                        !Object(_helpers__WEBPACK_IMPORTED_MODULE_5__["includes"])(bysecond, counterDate.second))) {
                    return [];
                }
                return ii.gettimeset(freq)(counterDate.hour, counterDate.minute, counterDate.second, counterDate.millisecond);
            }
//# sourceMappingURL=index.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iter/poslist.js":
        /*!*****************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iter/poslist.js ***!
  \*****************************************************/
        /*! exports provided: buildPoslist */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "buildPoslist", function() { return buildPoslist; });
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");


            function buildPoslist(bysetpos, timeset, start, end, ii, dayset) {
                var poslist = [];
                for (var j = 0; j < bysetpos.length; j++) {
                    var daypos = void 0;
                    var timepos = void 0;
                    var pos = bysetpos[j];
                    if (pos < 0) {
                        daypos = Math.floor(pos / timeset.length);
                        timepos = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["pymod"])(pos, timeset.length);
                    }
                    else {
                        daypos = Math.floor((pos - 1) / timeset.length);
                        timepos = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["pymod"])(pos - 1, timeset.length);
                    }
                    var tmp = [];
                    for (var k = start; k < end; k++) {
                        var val = dayset[k];
                        if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isPresent"])(val))
                            continue;
                        tmp.push(val);
                    }
                    var i = void 0;
                    if (daypos < 0) {
                        i = tmp.slice(daypos)[0];
                    }
                    else {
                        i = tmp[daypos];
                    }
                    var time = timeset[timepos];
                    var date = _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].fromOrdinal(ii.yearordinal + i);
                    var res = _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].combine(date, time);
                    // XXX: can this ever be in the array?
                    // - compare the actual date instead?
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["includes"])(poslist, res))
                        poslist.push(res);
                }
                _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].sort(poslist);
                return poslist;
            }
//# sourceMappingURL=poslist.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterinfo/easter.js":
        /*!********************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterinfo/easter.js ***!
  \********************************************************/
        /*! exports provided: easter */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "easter", function() { return easter; });
            function easter(y, offset) {
                if (offset === void 0) { offset = 0; }
                var a = y % 19;
                var b = Math.floor(y / 100);
                var c = y % 100;
                var d = Math.floor(b / 4);
                var e = b % 4;
                var f = Math.floor((b + 8) / 25);
                var g = Math.floor((b - f + 1) / 3);
                var h = Math.floor(19 * a + b - d - g + 15) % 30;
                var i = Math.floor(c / 4);
                var k = c % 4;
                var l = Math.floor(32 + 2 * e + 2 * i - h - k) % 7;
                var m = Math.floor((a + 11 * h + 22 * l) / 451);
                var month = Math.floor((h + l - 7 * m + 114) / 31);
                var day = ((h + l - 7 * m + 114) % 31) + 1;
                var date = Date.UTC(y, month - 1, day + offset);
                var yearStart = Date.UTC(y, 0, 1);
                return [Math.ceil((date - yearStart) / (1000 * 60 * 60 * 24))];
            }
//# sourceMappingURL=easter.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterinfo/index.js":
        /*!*******************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterinfo/index.js ***!
  \*******************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _yearinfo__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./yearinfo */ "./node_modules/rrule/dist/esm/iterinfo/yearinfo.js");
            /* harmony import */ var _monthinfo__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./monthinfo */ "./node_modules/rrule/dist/esm/iterinfo/monthinfo.js");
            /* harmony import */ var _easter__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./easter */ "./node_modules/rrule/dist/esm/iterinfo/easter.js");
            /* harmony import */ var _datetime__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../datetime */ "./node_modules/rrule/dist/esm/datetime.js");







// =============================================================================
// Iterinfo
// =============================================================================
            var Iterinfo = /** @class */ (function () {
                // eslint-disable-next-line no-empty-function
                function Iterinfo(options) {
                    this.options = options;
                }
                Iterinfo.prototype.rebuild = function (year, month) {
                    var options = this.options;
                    if (year !== this.lastyear) {
                        this.yearinfo = Object(_yearinfo__WEBPACK_IMPORTED_MODULE_3__["rebuildYear"])(year, options);
                    }
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["notEmpty"])(options.bynweekday) &&
                        (month !== this.lastmonth || year !== this.lastyear)) {
                        var _a = this.yearinfo, yearlen = _a.yearlen, mrange = _a.mrange, wdaymask = _a.wdaymask;
                        this.monthinfo = Object(_monthinfo__WEBPACK_IMPORTED_MODULE_4__["rebuildMonth"])(year, month, yearlen, mrange, wdaymask, options);
                    }
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isPresent"])(options.byeaster)) {
                        this.eastermask = Object(_easter__WEBPACK_IMPORTED_MODULE_5__["easter"])(year, options.byeaster);
                    }
                };
                Object.defineProperty(Iterinfo.prototype, "lastyear", {
                    get: function () {
                        return this.monthinfo ? this.monthinfo.lastyear : null;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "lastmonth", {
                    get: function () {
                        return this.monthinfo ? this.monthinfo.lastmonth : null;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "yearlen", {
                    get: function () {
                        return this.yearinfo.yearlen;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "yearordinal", {
                    get: function () {
                        return this.yearinfo.yearordinal;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "mrange", {
                    get: function () {
                        return this.yearinfo.mrange;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "wdaymask", {
                    get: function () {
                        return this.yearinfo.wdaymask;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "mmask", {
                    get: function () {
                        return this.yearinfo.mmask;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "wnomask", {
                    get: function () {
                        return this.yearinfo.wnomask;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "nwdaymask", {
                    get: function () {
                        return this.monthinfo ? this.monthinfo.nwdaymask : [];
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "nextyearlen", {
                    get: function () {
                        return this.yearinfo.nextyearlen;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "mdaymask", {
                    get: function () {
                        return this.yearinfo.mdaymask;
                    },
                    enumerable: false,
                    configurable: true
                });
                Object.defineProperty(Iterinfo.prototype, "nmdaymask", {
                    get: function () {
                        return this.yearinfo.nmdaymask;
                    },
                    enumerable: false,
                    configurable: true
                });
                Iterinfo.prototype.ydayset = function () {
                    return [Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(this.yearlen), 0, this.yearlen];
                };
                Iterinfo.prototype.mdayset = function (_, month) {
                    var start = this.mrange[month - 1];
                    var end = this.mrange[month];
                    var set = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(null, this.yearlen);
                    for (var i = start; i < end; i++)
                        set[i] = i;
                    return [set, start, end];
                };
                Iterinfo.prototype.wdayset = function (year, month, day) {
                    // We need to handle cross-year weeks here.
                    var set = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(null, this.yearlen + 7);
                    var i = _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].toOrdinal(new Date(Date.UTC(year, month - 1, day))) -
                        this.yearordinal;
                    var start = i;
                    for (var j = 0; j < 7; j++) {
                        set[i] = i;
                        ++i;
                        if (this.wdaymask[i] === this.options.wkst)
                            break;
                    }
                    return [set, start, i];
                };
                Iterinfo.prototype.ddayset = function (year, month, day) {
                    var set = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(null, this.yearlen);
                    var i = _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].toOrdinal(new Date(Date.UTC(year, month - 1, day))) -
                        this.yearordinal;
                    set[i] = i;
                    return [set, i, i + 1];
                };
                Iterinfo.prototype.htimeset = function (hour, _, second, millisecond) {
                    var _this = this;
                    var set = [];
                    this.options.byminute.forEach(function (minute) {
                        set = set.concat(_this.mtimeset(hour, minute, second, millisecond));
                    });
                    _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].sort(set);
                    return set;
                };
                Iterinfo.prototype.mtimeset = function (hour, minute, _, millisecond) {
                    var set = this.options.bysecond.map(function (second) { return new _datetime__WEBPACK_IMPORTED_MODULE_6__["Time"](hour, minute, second, millisecond); });
                    _dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].sort(set);
                    return set;
                };
                Iterinfo.prototype.stimeset = function (hour, minute, second, millisecond) {
                    return [new _datetime__WEBPACK_IMPORTED_MODULE_6__["Time"](hour, minute, second, millisecond)];
                };
                Iterinfo.prototype.getdayset = function (freq) {
                    switch (freq) {
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].YEARLY:
                            return this.ydayset.bind(this);
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].MONTHLY:
                            return this.mdayset.bind(this);
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].WEEKLY:
                            return this.wdayset.bind(this);
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].DAILY:
                            return this.ddayset.bind(this);
                        default:
                            return this.ddayset.bind(this);
                    }
                };
                Iterinfo.prototype.gettimeset = function (freq) {
                    switch (freq) {
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].HOURLY:
                            return this.htimeset.bind(this);
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].MINUTELY:
                            return this.mtimeset.bind(this);
                        case _types__WEBPACK_IMPORTED_MODULE_2__["Frequency"].SECONDLY:
                            return this.stimeset.bind(this);
                    }
                };
                return Iterinfo;
            }());
            /* harmony default export */ __webpack_exports__["default"] = (Iterinfo);
//# sourceMappingURL=index.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterinfo/monthinfo.js":
        /*!***********************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterinfo/monthinfo.js ***!
  \***********************************************************/
        /*! exports provided: rebuildMonth */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "rebuildMonth", function() { return rebuildMonth; });
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");


            function rebuildMonth(year, month, yearlen, mrange, wdaymask, options) {
                var result = {
                    lastyear: year,
                    lastmonth: month,
                    nwdaymask: [],
                };
                var ranges = [];
                if (options.freq === _rrule__WEBPACK_IMPORTED_MODULE_0__["RRule"].YEARLY) {
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["empty"])(options.bymonth)) {
                        ranges = [[0, yearlen]];
                    }
                    else {
                        for (var j = 0; j < options.bymonth.length; j++) {
                            month = options.bymonth[j];
                            ranges.push(mrange.slice(month - 1, month + 1));
                        }
                    }
                }
                else if (options.freq === _rrule__WEBPACK_IMPORTED_MODULE_0__["RRule"].MONTHLY) {
                    ranges = [mrange.slice(month - 1, month + 1)];
                }
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["empty"])(ranges)) {
                    return result;
                }
                // Weekly frequency won't get here, so we may not
                // care about cross-year weekly periods.
                result.nwdaymask = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(0, yearlen);
                for (var j = 0; j < ranges.length; j++) {
                    var rang = ranges[j];
                    var first = rang[0];
                    var last = rang[1] - 1;
                    for (var k = 0; k < options.bynweekday.length; k++) {
                        var i = void 0;
                        var _a = options.bynweekday[k], wday = _a[0], n = _a[1];
                        if (n < 0) {
                            i = last + (n + 1) * 7;
                            i -= Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["pymod"])(wdaymask[i] - wday, 7);
                        }
                        else {
                            i = first + (n - 1) * 7;
                            i += Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["pymod"])(7 - wdaymask[i] + wday, 7);
                        }
                        if (first <= i && i <= last)
                            result.nwdaymask[i] = 1;
                    }
                }
                return result;
            }
//# sourceMappingURL=monthinfo.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterinfo/yearinfo.js":
        /*!**********************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterinfo/yearinfo.js ***!
  \**********************************************************/
        /*! exports provided: rebuildYear */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "rebuildYear", function() { return rebuildYear; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _masks__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../masks */ "./node_modules/rrule/dist/esm/masks.js");




            function rebuildYear(year, options) {
                var firstyday = new Date(Date.UTC(year, 0, 1));
                var yearlen = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].isLeapYear(year) ? 366 : 365;
                var nextyearlen = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].isLeapYear(year + 1) ? 366 : 365;
                var yearordinal = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].toOrdinal(firstyday);
                var yearweekday = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].getWeekday(firstyday);
                var result = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({ yearlen: yearlen, nextyearlen: nextyearlen, yearordinal: yearordinal, yearweekday: yearweekday }, baseYearMasks(year)), { wnomask: null });
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["empty"])(options.byweekno)) {
                    return result;
                }
                result.wnomask = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["repeat"])(0, yearlen + 7);
                var firstwkst;
                var wyearlen;
                var no1wkst = (firstwkst = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(7 - yearweekday + options.wkst, 7));
                if (no1wkst >= 4) {
                    no1wkst = 0;
                    // Number of days in the year, plus the days we got
                    // from last year.
                    wyearlen = result.yearlen + Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(yearweekday - options.wkst, 7);
                }
                else {
                    // Number of days in the year, minus the days we
                    // left in last year.
                    wyearlen = yearlen - no1wkst;
                }
                var div = Math.floor(wyearlen / 7);
                var mod = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(wyearlen, 7);
                var numweeks = Math.floor(div + mod / 4);
                for (var j = 0; j < options.byweekno.length; j++) {
                    var n = options.byweekno[j];
                    if (n < 0) {
                        n += numweeks + 1;
                    }
                    if (!(n > 0 && n <= numweeks)) {
                        continue;
                    }
                    var i = void 0;
                    if (n > 1) {
                        i = no1wkst + (n - 1) * 7;
                        if (no1wkst !== firstwkst) {
                            i -= 7 - firstwkst;
                        }
                    }
                    else {
                        i = no1wkst;
                    }
                    for (var k = 0; k < 7; k++) {
                        result.wnomask[i] = 1;
                        i++;
                        if (result.wdaymask[i] === options.wkst)
                            break;
                    }
                }
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(options.byweekno, 1)) {
                    // Check week number 1 of next year as well
                    // orig-TODO : Check -numweeks for next year.
                    var i = no1wkst + numweeks * 7;
                    if (no1wkst !== firstwkst)
                        i -= 7 - firstwkst;
                    if (i < yearlen) {
                        // If week starts in next year, we
                        // don't care about it.
                        for (var j = 0; j < 7; j++) {
                            result.wnomask[i] = 1;
                            i += 1;
                            if (result.wdaymask[i] === options.wkst)
                                break;
                        }
                    }
                }
                if (no1wkst) {
                    // Check last week number of last year as
                    // well. If no1wkst is 0, either the year
                    // started on week start, or week number 1
                    // got days from last year, so there are no
                    // days from last year's last week number in
                    // this year.
                    var lnumweeks = void 0;
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(options.byweekno, -1)) {
                        var lyearweekday = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].getWeekday(new Date(Date.UTC(year - 1, 0, 1)));
                        var lno1wkst = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(7 - lyearweekday.valueOf() + options.wkst, 7);
                        var lyearlen = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].isLeapYear(year - 1) ? 366 : 365;
                        var weekst = void 0;
                        if (lno1wkst >= 4) {
                            lno1wkst = 0;
                            weekst = lyearlen + Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(lyearweekday - options.wkst, 7);
                        }
                        else {
                            weekst = yearlen - no1wkst;
                        }
                        lnumweeks = Math.floor(52 + Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["pymod"])(weekst, 7) / 4);
                    }
                    else {
                        lnumweeks = -1;
                    }
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(options.byweekno, lnumweeks)) {
                        for (var i = 0; i < no1wkst; i++)
                            result.wnomask[i] = 1;
                    }
                }
                return result;
            }
            function baseYearMasks(year) {
                var yearlen = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].isLeapYear(year) ? 366 : 365;
                var firstyday = new Date(Date.UTC(year, 0, 1));
                var wday = _dateutil__WEBPACK_IMPORTED_MODULE_1__["default"].getWeekday(firstyday);
                if (yearlen === 365) {
                    return {
                        mmask: _masks__WEBPACK_IMPORTED_MODULE_3__["M365MASK"],
                        mdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["MDAY365MASK"],
                        nmdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["NMDAY365MASK"],
                        wdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["WDAYMASK"].slice(wday),
                        mrange: _masks__WEBPACK_IMPORTED_MODULE_3__["M365RANGE"],
                    };
                }
                return {
                    mmask: _masks__WEBPACK_IMPORTED_MODULE_3__["M366MASK"],
                    mdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["MDAY366MASK"],
                    nmdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["NMDAY366MASK"],
                    wdaymask: _masks__WEBPACK_IMPORTED_MODULE_3__["WDAYMASK"].slice(wday),
                    mrange: _masks__WEBPACK_IMPORTED_MODULE_3__["M366RANGE"],
                };
            }
//# sourceMappingURL=yearinfo.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterresult.js":
        /*!***************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterresult.js ***!
  \***************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /**
             * This class helps us to emulate python's generators, sorta.
             */
            var IterResult = /** @class */ (function () {
                function IterResult(method, args) {
                    this.minDate = null;
                    this.maxDate = null;
                    this._result = [];
                    this.total = 0;
                    this.method = method;
                    this.args = args;
                    if (method === 'between') {
                        this.maxDate = args.inc
                            ? args.before
                            : new Date(args.before.getTime() - 1);
                        this.minDate = args.inc ? args.after : new Date(args.after.getTime() + 1);
                    }
                    else if (method === 'before') {
                        this.maxDate = args.inc ? args.dt : new Date(args.dt.getTime() - 1);
                    }
                    else if (method === 'after') {
                        this.minDate = args.inc ? args.dt : new Date(args.dt.getTime() + 1);
                    }
                }
                /**
                 * Possibly adds a date into the result.
                 *
                 * @param {Date} date - the date isn't necessarly added to the result
                 * list (if it is too late/too early)
                 * @return {Boolean} true if it makes sense to continue the iteration
                 * false if we're done.
                 */
                IterResult.prototype.accept = function (date) {
                    ++this.total;
                    var tooEarly = this.minDate && date < this.minDate;
                    var tooLate = this.maxDate && date > this.maxDate;
                    if (this.method === 'between') {
                        if (tooEarly)
                            return true;
                        if (tooLate)
                            return false;
                    }
                    else if (this.method === 'before') {
                        if (tooLate)
                            return false;
                    }
                    else if (this.method === 'after') {
                        if (tooEarly)
                            return true;
                        this.add(date);
                        return false;
                    }
                    return this.add(date);
                };
                /**
                 *
                 * @param {Date} date that is part of the result.
                 * @return {Boolean} whether we are interested in more values.
                 */
                IterResult.prototype.add = function (date) {
                    this._result.push(date);
                    return true;
                };
                /**
                 * 'before' and 'after' return only one date, whereas 'all'
                 * and 'between' an array.
                 *
                 * @return {Date,Array?}
                 */
                IterResult.prototype.getValue = function () {
                    var res = this._result;
                    switch (this.method) {
                        case 'all':
                        case 'between':
                            return res;
                        case 'before':
                        case 'after':
                        default:
                            return (res.length ? res[res.length - 1] : null);
                    }
                };
                IterResult.prototype.clone = function () {
                    return new IterResult(this.method, this.args);
                };
                return IterResult;
            }());
            /* harmony default export */ __webpack_exports__["default"] = (IterResult);
//# sourceMappingURL=iterresult.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/iterset.js":
        /*!************************************************!*\
  !*** ./node_modules/rrule/dist/esm/iterset.js ***!
  \************************************************/
        /*! exports provided: iterSet */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "iterSet", function() { return iterSet; });
            /* harmony import */ var _datewithzone__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./datewithzone */ "./node_modules/rrule/dist/esm/datewithzone.js");
            /* harmony import */ var _iter__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./iter */ "./node_modules/rrule/dist/esm/iter/index.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");



            function iterSet(iterResult, _rrule, _exrule, _rdate, _exdate, tzid) {
                var _exdateHash = {};
                var _accept = iterResult.accept;
                function evalExdate(after, before) {
                    _exrule.forEach(function (rrule) {
                        rrule.between(after, before, true).forEach(function (date) {
                            _exdateHash[Number(date)] = true;
                        });
                    });
                }
                _exdate.forEach(function (date) {
                    var zonedDate = new _datewithzone__WEBPACK_IMPORTED_MODULE_0__["DateWithZone"](date, tzid).rezonedDate();
                    _exdateHash[Number(zonedDate)] = true;
                });
                iterResult.accept = function (date) {
                    var dt = Number(date);
                    if (isNaN(dt))
                        return _accept.call(this, date);
                    if (!_exdateHash[dt]) {
                        evalExdate(new Date(dt - 1), new Date(dt + 1));
                        if (!_exdateHash[dt]) {
                            _exdateHash[dt] = true;
                            return _accept.call(this, date);
                        }
                    }
                    return true;
                };
                if (iterResult.method === 'between') {
                    evalExdate(iterResult.args.after, iterResult.args.before);
                    iterResult.accept = function (date) {
                        var dt = Number(date);
                        if (!_exdateHash[dt]) {
                            _exdateHash[dt] = true;
                            return _accept.call(this, date);
                        }
                        return true;
                    };
                }
                for (var i = 0; i < _rdate.length; i++) {
                    var zonedDate = new _datewithzone__WEBPACK_IMPORTED_MODULE_0__["DateWithZone"](_rdate[i], tzid).rezonedDate();
                    if (!iterResult.accept(new Date(zonedDate.getTime())))
                        break;
                }
                _rrule.forEach(function (rrule) {
                    Object(_iter__WEBPACK_IMPORTED_MODULE_1__["iter"])(iterResult, rrule.options);
                });
                var res = iterResult._result;
                _dateutil__WEBPACK_IMPORTED_MODULE_2__["default"].sort(res);
                switch (iterResult.method) {
                    case 'all':
                    case 'between':
                        return res;
                    case 'before':
                        return ((res.length && res[res.length - 1]) || null);
                    case 'after':
                    default:
                        return ((res.length && res[0]) || null);
                }
            }
//# sourceMappingURL=iterset.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/masks.js":
        /*!**********************************************!*\
  !*** ./node_modules/rrule/dist/esm/masks.js ***!
  \**********************************************/
        /*! exports provided: WDAYMASK, M365MASK, M365RANGE, M366MASK, M366RANGE, MDAY365MASK, MDAY366MASK, NMDAY365MASK, NMDAY366MASK */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WDAYMASK", function() { return WDAYMASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "M365MASK", function() { return M365MASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "M365RANGE", function() { return M365RANGE; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "M366MASK", function() { return M366MASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "M366RANGE", function() { return M366RANGE; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MDAY365MASK", function() { return MDAY365MASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MDAY366MASK", function() { return MDAY366MASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NMDAY365MASK", function() { return NMDAY365MASK; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NMDAY366MASK", function() { return NMDAY366MASK; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");


// =============================================================================
// Date masks
// =============================================================================
// Every mask is 7 days longer to handle cross-year weekly periods.
            var M365MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(1, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(2, 28), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(3, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(4, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(5, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(6, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(7, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(8, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(9, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(10, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(11, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(12, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(1, 7), true);
            var M366MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(1, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(2, 29), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(3, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(4, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(5, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(6, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(7, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(8, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(9, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(10, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(11, 30), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(12, 31), true), Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["repeat"])(1, 7), true);
            var M28 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(1, 29);
            var M29 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(1, 30);
            var M30 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(1, 31);
            var M31 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(1, 32);
            var MDAY366MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], M31, true), M29, true), M31, true), M30, true), M31, true), M30, true), M31, true), M31, true), M30, true), M31, true), M30, true), M31, true), M31.slice(0, 7), true);
            var MDAY365MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], M31, true), M28, true), M31, true), M30, true), M31, true), M30, true), M31, true), M31, true), M30, true), M31, true), M30, true), M31, true), M31.slice(0, 7), true);
            var NM28 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(-28, 0);
            var NM29 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(-29, 0);
            var NM30 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(-30, 0);
            var NM31 = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(-31, 0);
            var NMDAY366MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], NM31, true), NM29, true), NM31, true), NM30, true), NM31, true), NM30, true), NM31, true), NM31, true), NM30, true), NM31, true), NM30, true), NM31, true), NM31.slice(0, 7), true);
            var NMDAY365MASK = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__spreadArray"])([], NM31, true), NM28, true), NM31, true), NM30, true), NM31, true), NM30, true), NM31, true), NM31, true), NM30, true), NM31, true), NM30, true), NM31, true), NM31.slice(0, 7), true);
            var M366RANGE = [0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366];
            var M365RANGE = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365];
            var WDAYMASK = (function () {
                var wdaymask = [];
                for (var i = 0; i < 55; i++)
                    wdaymask = wdaymask.concat(Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["range"])(7));
                return wdaymask;
            })();

//# sourceMappingURL=masks.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/nlp/i18n.js":
        /*!*************************************************!*\
  !*** ./node_modules/rrule/dist/esm/nlp/i18n.js ***!
  \*************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
// =============================================================================
// i18n
// =============================================================================
            var ENGLISH = {
                dayNames: [
                    'Sunday',
                    'Monday',
                    'Tuesday',
                    'Wednesday',
                    'Thursday',
                    'Friday',
                    'Saturday',
                ],
                monthNames: [
                    'January',
                    'February',
                    'March',
                    'April',
                    'May',
                    'June',
                    'July',
                    'August',
                    'September',
                    'October',
                    'November',
                    'December',
                ],
                tokens: {
                    SKIP: /^[ \r\n\t]+|^\.$/,
                    number: /^[1-9][0-9]*/,
                    numberAsText: /^(one|two|three)/i,
                    every: /^every/i,
                    'day(s)': /^days?/i,
                    'weekday(s)': /^weekdays?/i,
                    'week(s)': /^weeks?/i,
                    'hour(s)': /^hours?/i,
                    'minute(s)': /^minutes?/i,
                    'month(s)': /^months?/i,
                    'year(s)': /^years?/i,
                    on: /^(on|in)/i,
                    at: /^(at)/i,
                    the: /^the/i,
                    first: /^first/i,
                    second: /^second/i,
                    third: /^third/i,
                    nth: /^([1-9][0-9]*)(\.|th|nd|rd|st)/i,
                    last: /^last/i,
                    for: /^for/i,
                    'time(s)': /^times?/i,
                    until: /^(un)?til/i,
                    monday: /^mo(n(day)?)?/i,
                    tuesday: /^tu(e(s(day)?)?)?/i,
                    wednesday: /^we(d(n(esday)?)?)?/i,
                    thursday: /^th(u(r(sday)?)?)?/i,
                    friday: /^fr(i(day)?)?/i,
                    saturday: /^sa(t(urday)?)?/i,
                    sunday: /^su(n(day)?)?/i,
                    january: /^jan(uary)?/i,
                    february: /^feb(ruary)?/i,
                    march: /^mar(ch)?/i,
                    april: /^apr(il)?/i,
                    may: /^may/i,
                    june: /^june?/i,
                    july: /^july?/i,
                    august: /^aug(ust)?/i,
                    september: /^sep(t(ember)?)?/i,
                    october: /^oct(ober)?/i,
                    november: /^nov(ember)?/i,
                    december: /^dec(ember)?/i,
                    comma: /^(,\s*|(and|or)\s*)+/i,
                },
            };
            /* harmony default export */ __webpack_exports__["default"] = (ENGLISH);
//# sourceMappingURL=i18n.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/nlp/index.js":
        /*!**************************************************!*\
  !*** ./node_modules/rrule/dist/esm/nlp/index.js ***!
  \**************************************************/
        /*! exports provided: fromText, parseText, isFullyConvertible, toText */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "fromText", function() { return fromText; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isFullyConvertible", function() { return isFullyConvertible; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "toText", function() { return toText; });
            /* harmony import */ var _totext__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./totext */ "./node_modules/rrule/dist/esm/nlp/totext.js");
            /* harmony import */ var _parsetext__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./parsetext */ "./node_modules/rrule/dist/esm/nlp/parsetext.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseText", function() { return _parsetext__WEBPACK_IMPORTED_MODULE_1__["default"]; });

            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _i18n__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./i18n */ "./node_modules/rrule/dist/esm/nlp/i18n.js");





            /* !
 * rrule.js - Library for working with recurrence rules for calendar dates.
 * https://github.com/jakubroztocil/rrule
 *
 * Copyright 2010, Jakub Roztocil and Lars Schoning
 * Licenced under the BSD licence.
 * https://github.com/jakubroztocil/rrule/blob/master/LICENCE
 *
 */
            /**
             *
             * Implementation of RRule.fromText() and RRule::toText().
             *
             *
             * On the client side, this file needs to be included
             * when those functions are used.
             *
             */
// =============================================================================
// fromText
// =============================================================================
            /**
             * Will be able to convert some of the below described rules from
             * text format to a rule object.
             *
             *
             * RULES
             *
             * Every ([n])
             * day(s)
             * | [weekday], ..., (and) [weekday]
             * | weekday(s)
             * | week(s)
             * | month(s)
             * | [month], ..., (and) [month]
             * | year(s)
             *
             *
             * Plus 0, 1, or multiple of these:
             *
             * on [weekday], ..., (or) [weekday] the [monthday], [monthday], ... (or) [monthday]
             *
             * on [weekday], ..., (and) [weekday]
             *
             * on the [monthday], [monthday], ... (and) [monthday] (day of the month)
             *
             * on the [nth-weekday], ..., (and) [nth-weekday] (of the month/year)
             *
             *
             * Plus 0 or 1 of these:
             *
             * for [n] time(s)
             *
             * until [date]
             *
             * Plus (.)
             *
             *
             * Definitely no supported for parsing:
             *
             * (for year):
             * in week(s) [n], ..., (and) [n]
             *
             * on the [yearday], ..., (and) [n] day of the year
             * on day [yearday], ..., (and) [n]
             *
             *
             * NON-TERMINALS
             *
             * [n]: 1, 2 ..., one, two, three ..
             * [month]: January, February, March, April, May, ... December
             * [weekday]: Monday, ... Sunday
             * [nth-weekday]: first [weekday], 2nd [weekday], ... last [weekday], ...
             * [monthday]: first, 1., 2., 1st, 2nd, second, ... 31st, last day, 2nd last day, ..
             * [date]:
             * - [month] (0-31(,) ([year])),
             * - (the) 0-31.(1-12.([year])),
             * - (the) 0-31/(1-12/([year])),
             * - [weekday]
             *
             * [year]: 0000, 0001, ... 01, 02, ..
             *
             * Definitely not supported for parsing:
             *
             * [yearday]: first, 1., 2., 1st, 2nd, second, ... 366th, last day, 2nd last day, ..
             *
             * @param {String} text
             * @return {Object, Boolean} the rule, or null.
             */
            var fromText = function (text, language) {
                if (language === void 0) { language = _i18n__WEBPACK_IMPORTED_MODULE_4__["default"]; }
                return new _rrule__WEBPACK_IMPORTED_MODULE_2__["RRule"](Object(_parsetext__WEBPACK_IMPORTED_MODULE_1__["default"])(text, language) || undefined);
            };
            var common = [
                'count',
                'until',
                'interval',
                'byweekday',
                'bymonthday',
                'bymonth',
            ];
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED = [];
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].HOURLY] = common;
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].MINUTELY] = common;
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].DAILY] = ['byhour'].concat(common);
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].WEEKLY] = common;
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].MONTHLY] = common;
            _totext__WEBPACK_IMPORTED_MODULE_0__["default"].IMPLEMENTED[_types__WEBPACK_IMPORTED_MODULE_3__["Frequency"].YEARLY] = ['byweekno', 'byyearday'].concat(common);
// =============================================================================
// Export
// =============================================================================
            var toText = function (rrule, gettext, language, dateFormatter) {
                return new _totext__WEBPACK_IMPORTED_MODULE_0__["default"](rrule, gettext, language, dateFormatter).toString();
            };
            var isFullyConvertible = _totext__WEBPACK_IMPORTED_MODULE_0__["default"].isFullyConvertible;

//# sourceMappingURL=index.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/nlp/parsetext.js":
        /*!******************************************************!*\
  !*** ./node_modules/rrule/dist/esm/nlp/parsetext.js ***!
  \******************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "default", function() { return parseText; });
            /* harmony import */ var _i18n__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./i18n */ "./node_modules/rrule/dist/esm/nlp/i18n.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../rrule */ "./node_modules/rrule/dist/esm/rrule.js");


// =============================================================================
// Parser
// =============================================================================
            var Parser = /** @class */ (function () {
                function Parser(rules) {
                    this.done = true;
                    this.rules = rules;
                }
                Parser.prototype.start = function (text) {
                    this.text = text;
                    this.done = false;
                    return this.nextSymbol();
                };
                Parser.prototype.isDone = function () {
                    return this.done && this.symbol === null;
                };
                Parser.prototype.nextSymbol = function () {
                    var best;
                    var bestSymbol;
                    this.symbol = null;
                    this.value = null;
                    do {
                        if (this.done)
                            return false;
                        var rule = void 0;
                        best = null;
                        for (var name_1 in this.rules) {
                            rule = this.rules[name_1];
                            var match = rule.exec(this.text);
                            if (match) {
                                if (best === null || match[0].length > best[0].length) {
                                    best = match;
                                    bestSymbol = name_1;
                                }
                            }
                        }
                        if (best != null) {
                            this.text = this.text.substr(best[0].length);
                            if (this.text === '')
                                this.done = true;
                        }
                        if (best == null) {
                            this.done = true;
                            this.symbol = null;
                            this.value = null;
                            return;
                        }
                    } while (bestSymbol === 'SKIP');
                    this.symbol = bestSymbol;
                    this.value = best;
                    return true;
                };
                Parser.prototype.accept = function (name) {
                    if (this.symbol === name) {
                        if (this.value) {
                            var v = this.value;
                            this.nextSymbol();
                            return v;
                        }
                        this.nextSymbol();
                        return true;
                    }
                    return false;
                };
                Parser.prototype.acceptNumber = function () {
                    return this.accept('number');
                };
                Parser.prototype.expect = function (name) {
                    if (this.accept(name))
                        return true;
                    throw new Error('expected ' + name + ' but found ' + this.symbol);
                };
                return Parser;
            }());
            function parseText(text, language) {
                if (language === void 0) { language = _i18n__WEBPACK_IMPORTED_MODULE_0__["default"]; }
                var options = {};
                var ttr = new Parser(language.tokens);
                if (!ttr.start(text))
                    return null;
                S();
                return options;
                function S() {
                    // every [n]
                    ttr.expect('every');
                    var n = ttr.acceptNumber();
                    if (n)
                        options.interval = parseInt(n[0], 10);
                    if (ttr.isDone())
                        throw new Error('Unexpected end');
                    switch (ttr.symbol) {
                        case 'day(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].DAILY;
                            if (ttr.nextSymbol()) {
                                AT();
                                F();
                            }
                            break;
                        // FIXME Note: every 2 weekdays != every two weeks on weekdays.
                        // DAILY on weekdays is not a valid rule
                        case 'weekday(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].WEEKLY;
                            options.byweekday = [_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].MO, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].TU, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].WE, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].TH, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].FR];
                            ttr.nextSymbol();
                            F();
                            break;
                        case 'week(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].WEEKLY;
                            if (ttr.nextSymbol()) {
                                ON();
                                F();
                            }
                            break;
                        case 'hour(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].HOURLY;
                            if (ttr.nextSymbol()) {
                                ON();
                                F();
                            }
                            break;
                        case 'minute(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].MINUTELY;
                            if (ttr.nextSymbol()) {
                                ON();
                                F();
                            }
                            break;
                        case 'month(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].MONTHLY;
                            if (ttr.nextSymbol()) {
                                ON();
                                F();
                            }
                            break;
                        case 'year(s)':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].YEARLY;
                            if (ttr.nextSymbol()) {
                                ON();
                                F();
                            }
                            break;
                        case 'monday':
                        case 'tuesday':
                        case 'wednesday':
                        case 'thursday':
                        case 'friday':
                        case 'saturday':
                        case 'sunday':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].WEEKLY;
                            var key = ttr.symbol
                                .substr(0, 2)
                                .toUpperCase();
                            options.byweekday = [_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"][key]];
                            if (!ttr.nextSymbol())
                                return;
                            // TODO check for duplicates
                            while (ttr.accept('comma')) {
                                if (ttr.isDone())
                                    throw new Error('Unexpected end');
                                var wkd = decodeWKD();
                                if (!wkd) {
                                    throw new Error('Unexpected symbol ' + ttr.symbol + ', expected weekday');
                                }
                                options.byweekday.push(_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"][wkd]);
                                ttr.nextSymbol();
                            }
                            MDAYs();
                            F();
                            break;
                        case 'january':
                        case 'february':
                        case 'march':
                        case 'april':
                        case 'may':
                        case 'june':
                        case 'july':
                        case 'august':
                        case 'september':
                        case 'october':
                        case 'november':
                        case 'december':
                            options.freq = _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].YEARLY;
                            options.bymonth = [decodeM()];
                            if (!ttr.nextSymbol())
                                return;
                            // TODO check for duplicates
                            while (ttr.accept('comma')) {
                                if (ttr.isDone())
                                    throw new Error('Unexpected end');
                                var m = decodeM();
                                if (!m) {
                                    throw new Error('Unexpected symbol ' + ttr.symbol + ', expected month');
                                }
                                options.bymonth.push(m);
                                ttr.nextSymbol();
                            }
                            ON();
                            F();
                            break;
                        default:
                            throw new Error('Unknown symbol');
                    }
                }
                function ON() {
                    var on = ttr.accept('on');
                    var the = ttr.accept('the');
                    if (!(on || the))
                        return;
                    do {
                        var nth = decodeNTH();
                        var wkd = decodeWKD();
                        var m = decodeM();
                        // nth <weekday> | <weekday>
                        if (nth) {
                            // ttr.nextSymbol()
                            if (wkd) {
                                ttr.nextSymbol();
                                if (!options.byweekday)
                                    options.byweekday = [];
                                options.byweekday.push(_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"][wkd].nth(nth));
                            }
                            else {
                                if (!options.bymonthday)
                                    options.bymonthday = [];
                                options.bymonthday.push(nth);
                                ttr.accept('day(s)');
                            }
                            // <weekday>
                        }
                        else if (wkd) {
                            ttr.nextSymbol();
                            if (!options.byweekday)
                                options.byweekday = [];
                            options.byweekday.push(_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"][wkd]);
                        }
                        else if (ttr.symbol === 'weekday(s)') {
                            ttr.nextSymbol();
                            if (!options.byweekday) {
                                options.byweekday = [_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].MO, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].TU, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].WE, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].TH, _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].FR];
                            }
                        }
                        else if (ttr.symbol === 'week(s)') {
                            ttr.nextSymbol();
                            var n = ttr.acceptNumber();
                            if (!n) {
                                throw new Error('Unexpected symbol ' + ttr.symbol + ', expected week number');
                            }
                            options.byweekno = [parseInt(n[0], 10)];
                            while (ttr.accept('comma')) {
                                n = ttr.acceptNumber();
                                if (!n) {
                                    throw new Error('Unexpected symbol ' + ttr.symbol + '; expected monthday');
                                }
                                options.byweekno.push(parseInt(n[0], 10));
                            }
                        }
                        else if (m) {
                            ttr.nextSymbol();
                            if (!options.bymonth)
                                options.bymonth = [];
                            options.bymonth.push(m);
                        }
                        else {
                            return;
                        }
                    } while (ttr.accept('comma') || ttr.accept('the') || ttr.accept('on'));
                }
                function AT() {
                    var at = ttr.accept('at');
                    if (!at)
                        return;
                    do {
                        var n = ttr.acceptNumber();
                        if (!n) {
                            throw new Error('Unexpected symbol ' + ttr.symbol + ', expected hour');
                        }
                        options.byhour = [parseInt(n[0], 10)];
                        while (ttr.accept('comma')) {
                            n = ttr.acceptNumber();
                            if (!n) {
                                throw new Error('Unexpected symbol ' + ttr.symbol + '; expected hour');
                            }
                            options.byhour.push(parseInt(n[0], 10));
                        }
                    } while (ttr.accept('comma') || ttr.accept('at'));
                }
                function decodeM() {
                    switch (ttr.symbol) {
                        case 'january':
                            return 1;
                        case 'february':
                            return 2;
                        case 'march':
                            return 3;
                        case 'april':
                            return 4;
                        case 'may':
                            return 5;
                        case 'june':
                            return 6;
                        case 'july':
                            return 7;
                        case 'august':
                            return 8;
                        case 'september':
                            return 9;
                        case 'october':
                            return 10;
                        case 'november':
                            return 11;
                        case 'december':
                            return 12;
                        default:
                            return false;
                    }
                }
                function decodeWKD() {
                    switch (ttr.symbol) {
                        case 'monday':
                        case 'tuesday':
                        case 'wednesday':
                        case 'thursday':
                        case 'friday':
                        case 'saturday':
                        case 'sunday':
                            return ttr.symbol.substr(0, 2).toUpperCase();
                        default:
                            return false;
                    }
                }
                function decodeNTH() {
                    switch (ttr.symbol) {
                        case 'last':
                            ttr.nextSymbol();
                            return -1;
                        case 'first':
                            ttr.nextSymbol();
                            return 1;
                        case 'second':
                            ttr.nextSymbol();
                            return ttr.accept('last') ? -2 : 2;
                        case 'third':
                            ttr.nextSymbol();
                            return ttr.accept('last') ? -3 : 3;
                        case 'nth':
                            var v = parseInt(ttr.value[1], 10);
                            if (v < -366 || v > 366)
                                throw new Error('Nth out of range: ' + v);
                            ttr.nextSymbol();
                            return ttr.accept('last') ? -v : v;
                        default:
                            return false;
                    }
                }
                function MDAYs() {
                    ttr.accept('on');
                    ttr.accept('the');
                    var nth = decodeNTH();
                    if (!nth)
                        return;
                    options.bymonthday = [nth];
                    ttr.nextSymbol();
                    while (ttr.accept('comma')) {
                        nth = decodeNTH();
                        if (!nth) {
                            throw new Error('Unexpected symbol ' + ttr.symbol + '; expected monthday');
                        }
                        options.bymonthday.push(nth);
                        ttr.nextSymbol();
                    }
                }
                function F() {
                    if (ttr.symbol === 'until') {
                        var date = Date.parse(ttr.text);
                        if (!date)
                            throw new Error('Cannot parse until date:' + ttr.text);
                        options.until = new Date(date);
                    }
                    else if (ttr.accept('for')) {
                        options.count = parseInt(ttr.value[0], 10);
                        ttr.expect('number');
                        // ttr.expect('times')
                    }
                }
            }
//# sourceMappingURL=parsetext.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/nlp/totext.js":
        /*!***************************************************!*\
  !*** ./node_modules/rrule/dist/esm/nlp/totext.js ***!
  \***************************************************/
        /*! exports provided: default */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony import */ var _i18n__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./i18n */ "./node_modules/rrule/dist/esm/nlp/i18n.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../helpers */ "./node_modules/rrule/dist/esm/helpers.js");



// =============================================================================
// Helper functions
// =============================================================================
            /**
             * Return true if a value is in an array
             */
            var contains = function (arr, val) {
                return arr.indexOf(val) !== -1;
            };
            var defaultGetText = function (id) { return id.toString(); };
            var defaultDateFormatter = function (year, month, day) { return "".concat(month, " ").concat(day, ", ").concat(year); };
            /**
             *
             * @param {RRule} rrule
             * Optional:
             * @param {Function} gettext function
             * @param {Object} language definition
             * @constructor
             */
            var ToText = /** @class */ (function () {
                function ToText(rrule, gettext, language, dateFormatter) {
                    if (gettext === void 0) { gettext = defaultGetText; }
                    if (language === void 0) { language = _i18n__WEBPACK_IMPORTED_MODULE_0__["default"]; }
                    if (dateFormatter === void 0) { dateFormatter = defaultDateFormatter; }
                    this.text = [];
                    this.language = language || _i18n__WEBPACK_IMPORTED_MODULE_0__["default"];
                    this.gettext = gettext;
                    this.dateFormatter = dateFormatter;
                    this.rrule = rrule;
                    this.options = rrule.options;
                    this.origOptions = rrule.origOptions;
                    if (this.origOptions.bymonthday) {
                        var bymonthday = [].concat(this.options.bymonthday);
                        var bynmonthday = [].concat(this.options.bynmonthday);
                        bymonthday.sort(function (a, b) { return a - b; });
                        bynmonthday.sort(function (a, b) { return b - a; });
                        // 1, 2, 3, .., -5, -4, -3, ..
                        this.bymonthday = bymonthday.concat(bynmonthday);
                        if (!this.bymonthday.length)
                            this.bymonthday = null;
                    }
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(this.origOptions.byweekday)) {
                        var byweekday = !Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(this.origOptions.byweekday)
                            ? [this.origOptions.byweekday]
                            : this.origOptions.byweekday;
                        var days = String(byweekday);
                        this.byweekday = {
                            allWeeks: byweekday.filter(function (weekday) {
                                return !weekday.n;
                            }),
                            someWeeks: byweekday.filter(function (weekday) {
                                return Boolean(weekday.n);
                            }),
                            isWeekdays: days.indexOf('MO') !== -1 &&
                                days.indexOf('TU') !== -1 &&
                                days.indexOf('WE') !== -1 &&
                                days.indexOf('TH') !== -1 &&
                                days.indexOf('FR') !== -1 &&
                                days.indexOf('SA') === -1 &&
                                days.indexOf('SU') === -1,
                            isEveryDay: days.indexOf('MO') !== -1 &&
                                days.indexOf('TU') !== -1 &&
                                days.indexOf('WE') !== -1 &&
                                days.indexOf('TH') !== -1 &&
                                days.indexOf('FR') !== -1 &&
                                days.indexOf('SA') !== -1 &&
                                days.indexOf('SU') !== -1,
                        };
                        var sortWeekDays = function (a, b) {
                            return a.weekday - b.weekday;
                        };
                        this.byweekday.allWeeks.sort(sortWeekDays);
                        this.byweekday.someWeeks.sort(sortWeekDays);
                        if (!this.byweekday.allWeeks.length)
                            this.byweekday.allWeeks = null;
                        if (!this.byweekday.someWeeks.length)
                            this.byweekday.someWeeks = null;
                    }
                    else {
                        this.byweekday = null;
                    }
                }
                /**
                 * Test whether the rrule can be fully converted to text.
                 *
                 * @param {RRule} rrule
                 * @return {Boolean}
                 */
                ToText.isFullyConvertible = function (rrule) {
                    var canConvert = true;
                    if (!(rrule.options.freq in ToText.IMPLEMENTED))
                        return false;
                    if (rrule.origOptions.until && rrule.origOptions.count)
                        return false;
                    for (var key in rrule.origOptions) {
                        if (contains(['dtstart', 'wkst', 'freq'], key))
                            return true;
                        if (!contains(ToText.IMPLEMENTED[rrule.options.freq], key))
                            return false;
                    }
                    return canConvert;
                };
                ToText.prototype.isFullyConvertible = function () {
                    return ToText.isFullyConvertible(this.rrule);
                };
                /**
                 * Perform the conversion. Only some of the frequencies are supported.
                 * If some of the rrule's options aren't supported, they'll
                 * be omitted from the output an "(~ approximate)" will be appended.
                 *
                 * @return {*}
                 */
                ToText.prototype.toString = function () {
                    var gettext = this.gettext;
                    if (!(this.options.freq in ToText.IMPLEMENTED)) {
                        return gettext('RRule error: Unable to fully convert this rrule to text');
                    }
                    this.text = [gettext('every')];
                    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                    // @ts-ignore
                    this[_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"].FREQUENCIES[this.options.freq]]();
                    if (this.options.until) {
                        this.add(gettext('until'));
                        var until = this.options.until;
                        this.add(this.dateFormatter(until.getUTCFullYear(), this.language.monthNames[until.getUTCMonth()], until.getUTCDate()));
                    }
                    else if (this.options.count) {
                        this.add(gettext('for'))
                            .add(this.options.count.toString())
                            .add(this.plural(this.options.count) ? gettext('times') : gettext('time'));
                    }
                    if (!this.isFullyConvertible())
                        this.add(gettext('(~ approximate)'));
                    return this.text.join('');
                };
                ToText.prototype.HOURLY = function () {
                    var gettext = this.gettext;
                    if (this.options.interval !== 1)
                        this.add(this.options.interval.toString());
                    this.add(this.plural(this.options.interval) ? gettext('hours') : gettext('hour'));
                };
                ToText.prototype.MINUTELY = function () {
                    var gettext = this.gettext;
                    if (this.options.interval !== 1)
                        this.add(this.options.interval.toString());
                    this.add(this.plural(this.options.interval)
                        ? gettext('minutes')
                        : gettext('minute'));
                };
                ToText.prototype.DAILY = function () {
                    var gettext = this.gettext;
                    if (this.options.interval !== 1)
                        this.add(this.options.interval.toString());
                    if (this.byweekday && this.byweekday.isWeekdays) {
                        this.add(this.plural(this.options.interval)
                            ? gettext('weekdays')
                            : gettext('weekday'));
                    }
                    else {
                        this.add(this.plural(this.options.interval) ? gettext('days') : gettext('day'));
                    }
                    if (this.origOptions.bymonth) {
                        this.add(gettext('in'));
                        this._bymonth();
                    }
                    if (this.bymonthday) {
                        this._bymonthday();
                    }
                    else if (this.byweekday) {
                        this._byweekday();
                    }
                    else if (this.origOptions.byhour) {
                        this._byhour();
                    }
                };
                ToText.prototype.WEEKLY = function () {
                    var gettext = this.gettext;
                    if (this.options.interval !== 1) {
                        this.add(this.options.interval.toString()).add(this.plural(this.options.interval) ? gettext('weeks') : gettext('week'));
                    }
                    if (this.byweekday && this.byweekday.isWeekdays) {
                        if (this.options.interval === 1) {
                            this.add(this.plural(this.options.interval)
                                ? gettext('weekdays')
                                : gettext('weekday'));
                        }
                        else {
                            this.add(gettext('on')).add(gettext('weekdays'));
                        }
                    }
                    else if (this.byweekday && this.byweekday.isEveryDay) {
                        this.add(this.plural(this.options.interval) ? gettext('days') : gettext('day'));
                    }
                    else {
                        if (this.options.interval === 1)
                            this.add(gettext('week'));
                        if (this.origOptions.bymonth) {
                            this.add(gettext('in'));
                            this._bymonth();
                        }
                        if (this.bymonthday) {
                            this._bymonthday();
                        }
                        else if (this.byweekday) {
                            this._byweekday();
                        }
                    }
                };
                ToText.prototype.MONTHLY = function () {
                    var gettext = this.gettext;
                    if (this.origOptions.bymonth) {
                        if (this.options.interval !== 1) {
                            this.add(this.options.interval.toString()).add(gettext('months'));
                            if (this.plural(this.options.interval))
                                this.add(gettext('in'));
                        }
                        else {
                            // this.add(gettext('MONTH'))
                        }
                        this._bymonth();
                    }
                    else {
                        if (this.options.interval !== 1) {
                            this.add(this.options.interval.toString());
                        }
                        this.add(this.plural(this.options.interval)
                            ? gettext('months')
                            : gettext('month'));
                    }
                    if (this.bymonthday) {
                        this._bymonthday();
                    }
                    else if (this.byweekday && this.byweekday.isWeekdays) {
                        this.add(gettext('on')).add(gettext('weekdays'));
                    }
                    else if (this.byweekday) {
                        this._byweekday();
                    }
                };
                ToText.prototype.YEARLY = function () {
                    var gettext = this.gettext;
                    if (this.origOptions.bymonth) {
                        if (this.options.interval !== 1) {
                            this.add(this.options.interval.toString());
                            this.add(gettext('years'));
                        }
                        else {
                            // this.add(gettext('YEAR'))
                        }
                        this._bymonth();
                    }
                    else {
                        if (this.options.interval !== 1) {
                            this.add(this.options.interval.toString());
                        }
                        this.add(this.plural(this.options.interval) ? gettext('years') : gettext('year'));
                    }
                    if (this.bymonthday) {
                        this._bymonthday();
                    }
                    else if (this.byweekday) {
                        this._byweekday();
                    }
                    if (this.options.byyearday) {
                        this.add(gettext('on the'))
                            .add(this.list(this.options.byyearday, this.nth, gettext('and')))
                            .add(gettext('day'));
                    }
                    if (this.options.byweekno) {
                        this.add(gettext('in'))
                            .add(this.plural(this.options.byweekno.length)
                                ? gettext('weeks')
                                : gettext('week'))
                            .add(this.list(this.options.byweekno, undefined, gettext('and')));
                    }
                };
                ToText.prototype._bymonthday = function () {
                    var gettext = this.gettext;
                    if (this.byweekday && this.byweekday.allWeeks) {
                        this.add(gettext('on'))
                            .add(this.list(this.byweekday.allWeeks, this.weekdaytext, gettext('or')))
                            .add(gettext('the'))
                            .add(this.list(this.bymonthday, this.nth, gettext('or')));
                    }
                    else {
                        this.add(gettext('on the')).add(this.list(this.bymonthday, this.nth, gettext('and')));
                    }
                    // this.add(gettext('DAY'))
                };
                ToText.prototype._byweekday = function () {
                    var gettext = this.gettext;
                    if (this.byweekday.allWeeks && !this.byweekday.isWeekdays) {
                        this.add(gettext('on')).add(this.list(this.byweekday.allWeeks, this.weekdaytext));
                    }
                    if (this.byweekday.someWeeks) {
                        if (this.byweekday.allWeeks)
                            this.add(gettext('and'));
                        this.add(gettext('on the')).add(this.list(this.byweekday.someWeeks, this.weekdaytext, gettext('and')));
                    }
                };
                ToText.prototype._byhour = function () {
                    var gettext = this.gettext;
                    this.add(gettext('at')).add(this.list(this.origOptions.byhour, undefined, gettext('and')));
                };
                ToText.prototype._bymonth = function () {
                    this.add(this.list(this.options.bymonth, this.monthtext, this.gettext('and')));
                };
                ToText.prototype.nth = function (n) {
                    n = parseInt(n.toString(), 10);
                    var nth;
                    var gettext = this.gettext;
                    if (n === -1)
                        return gettext('last');
                    var npos = Math.abs(n);
                    switch (npos) {
                        case 1:
                        case 21:
                        case 31:
                            nth = npos + gettext('st');
                            break;
                        case 2:
                        case 22:
                            nth = npos + gettext('nd');
                            break;
                        case 3:
                        case 23:
                            nth = npos + gettext('rd');
                            break;
                        default:
                            nth = npos + gettext('th');
                    }
                    return n < 0 ? nth + ' ' + gettext('last') : nth;
                };
                ToText.prototype.monthtext = function (m) {
                    return this.language.monthNames[m - 1];
                };
                ToText.prototype.weekdaytext = function (wday) {
                    var weekday = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(wday) ? (wday + 1) % 7 : wday.getJsWeekday();
                    return ((wday.n ? this.nth(wday.n) + ' ' : '') +
                        this.language.dayNames[weekday]);
                };
                ToText.prototype.plural = function (n) {
                    return n % 100 !== 1;
                };
                ToText.prototype.add = function (s) {
                    this.text.push(' ');
                    this.text.push(s);
                    return this;
                };
                ToText.prototype.list = function (arr, callback, finalDelim, delim) {
                    var _this = this;
                    if (delim === void 0) { delim = ','; }
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(arr)) {
                        arr = [arr];
                    }
                    var delimJoin = function (array, delimiter, finalDelimiter) {
                        var list = '';
                        for (var i = 0; i < array.length; i++) {
                            if (i !== 0) {
                                if (i === array.length - 1) {
                                    list += ' ' + finalDelimiter + ' ';
                                }
                                else {
                                    list += delimiter + ' ';
                                }
                            }
                            list += array[i];
                        }
                        return list;
                    };
                    callback =
                        callback ||
                        function (o) {
                            return o.toString();
                        };
                    var realCallback = function (arg) {
                        return callback && callback.call(_this, arg);
                    };
                    if (finalDelim) {
                        return delimJoin(arr.map(realCallback), delim, finalDelim);
                    }
                    else {
                        return arr.map(realCallback).join(delim + ' ');
                    }
                };
                return ToText;
            }());
            /* harmony default export */ __webpack_exports__["default"] = (ToText);
//# sourceMappingURL=totext.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/optionstostring.js":
        /*!********************************************************!*\
  !*** ./node_modules/rrule/dist/esm/optionstostring.js ***!
  \********************************************************/
        /*! exports provided: optionsToString */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "optionsToString", function() { return optionsToString; });
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _datewithzone__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./datewithzone */ "./node_modules/rrule/dist/esm/datewithzone.js");





            function optionsToString(options) {
                var rrule = [];
                var dtstart = '';
                var keys = Object.keys(options);
                var defaultKeys = Object.keys(_rrule__WEBPACK_IMPORTED_MODULE_0__["DEFAULT_OPTIONS"]);
                for (var i = 0; i < keys.length; i++) {
                    if (keys[i] === 'tzid')
                        continue;
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["includes"])(defaultKeys, keys[i]))
                        continue;
                    var key = keys[i].toUpperCase();
                    var value = options[keys[i]];
                    var outValue = '';
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isPresent"])(value) || (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isArray"])(value) && !value.length))
                        continue;
                    switch (key) {
                        case 'FREQ':
                            outValue = _rrule__WEBPACK_IMPORTED_MODULE_0__["RRule"].FREQUENCIES[options.freq];
                            break;
                        case 'WKST':
                            if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isNumber"])(value)) {
                                outValue = new _weekday__WEBPACK_IMPORTED_MODULE_2__["Weekday"](value).toString();
                            }
                            else {
                                outValue = value.toString();
                            }
                            break;
                        case 'BYWEEKDAY':
                            /*
                  NOTE: BYWEEKDAY is a special case.
                  RRule() deconstructs the rule.options.byweekday array
                  into an array of Weekday arguments.
                  On the other hand, rule.origOptions is an array of Weekdays.
                  We need to handle both cases here.
                  It might be worth change RRule to keep the Weekdays.

                  Also, BYWEEKDAY (used by RRule) vs. BYDAY (RFC)

                  */
                            key = 'BYDAY';
                            outValue = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["toArray"])(value)
                                .map(function (wday) {
                                    if (wday instanceof _weekday__WEBPACK_IMPORTED_MODULE_2__["Weekday"]) {
                                        return wday;
                                    }
                                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isArray"])(wday)) {
                                        return new _weekday__WEBPACK_IMPORTED_MODULE_2__["Weekday"](wday[0], wday[1]);
                                    }
                                    return new _weekday__WEBPACK_IMPORTED_MODULE_2__["Weekday"](wday);
                                })
                                .toString();
                            break;
                        case 'DTSTART':
                            dtstart = buildDtstart(value, options.tzid);
                            break;
                        case 'UNTIL':
                            outValue = _dateutil__WEBPACK_IMPORTED_MODULE_3__["default"].timeToUntilString(value, !options.tzid);
                            break;
                        default:
                            if (Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["isArray"])(value)) {
                                var strValues = [];
                                for (var j = 0; j < value.length; j++) {
                                    strValues[j] = String(value[j]);
                                }
                                outValue = strValues.toString();
                            }
                            else {
                                outValue = String(value);
                            }
                    }
                    if (outValue) {
                        rrule.push([key, outValue]);
                    }
                }
                var rules = rrule
                    .map(function (_a) {
                        var key = _a[0], value = _a[1];
                        return "".concat(key, "=").concat(value.toString());
                    })
                    .join(';');
                var ruleString = '';
                if (rules !== '') {
                    ruleString = "RRULE:".concat(rules);
                }
                return [dtstart, ruleString].filter(function (x) { return !!x; }).join('\n');
            }
            function buildDtstart(dtstart, tzid) {
                if (!dtstart) {
                    return '';
                }
                return 'DTSTART' + new _datewithzone__WEBPACK_IMPORTED_MODULE_4__["DateWithZone"](new Date(dtstart), tzid).toString();
            }
//# sourceMappingURL=optionstostring.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/parseoptions.js":
        /*!*****************************************************!*\
  !*** ./node_modules/rrule/dist/esm/parseoptions.js ***!
  \*****************************************************/
        /*! exports provided: initializeOptions, parseOptions, buildTimeset */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "initializeOptions", function() { return initializeOptions; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseOptions", function() { return parseOptions; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "buildTimeset", function() { return buildTimeset; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
            /* harmony import */ var _datetime__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./datetime */ "./node_modules/rrule/dist/esm/datetime.js");







            function initializeOptions(options) {
                var invalid = [];
                var keys = Object.keys(options);
                // Shallow copy for options and origOptions and check for invalid
                for (var _i = 0, keys_1 = keys; _i < keys_1.length; _i++) {
                    var key = keys_1[_i];
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["includes"])(_rrule__WEBPACK_IMPORTED_MODULE_3__["defaultKeys"], key))
                        invalid.push(key);
                    if (_dateutil__WEBPACK_IMPORTED_MODULE_4__["default"].isDate(options[key]) && !_dateutil__WEBPACK_IMPORTED_MODULE_4__["default"].isValidDate(options[key])) {
                        invalid.push(key);
                    }
                }
                if (invalid.length) {
                    throw new Error('Invalid options: ' + invalid.join(', '));
                }
                return Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({}, options);
            }
            function parseOptions(options) {
                var opts = Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({}, _rrule__WEBPACK_IMPORTED_MODULE_3__["DEFAULT_OPTIONS"]), initializeOptions(options));
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byeaster))
                    opts.freq = _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].YEARLY;
                if (!(Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.freq) && _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].FREQUENCIES[opts.freq])) {
                    throw new Error("Invalid frequency: ".concat(opts.freq, " ").concat(options.freq));
                }
                if (!opts.dtstart)
                    opts.dtstart = new Date(new Date().setMilliseconds(0));
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.wkst)) {
                    opts.wkst = _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MO.weekday;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.wkst)) {
                    // cool, just keep it like that
                }
                else {
                    opts.wkst = opts.wkst.weekday;
                }
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.bysetpos)) {
                    if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.bysetpos))
                        opts.bysetpos = [opts.bysetpos];
                    for (var i = 0; i < opts.bysetpos.length; i++) {
                        var v = opts.bysetpos[i];
                        if (v === 0 || !(v >= -366 && v <= 366)) {
                            throw new Error('bysetpos must be between 1 and 366,' + ' or between -366 and -1');
                        }
                    }
                }
                if (!(Boolean(opts.byweekno) ||
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["notEmpty"])(opts.byweekno) ||
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["notEmpty"])(opts.byyearday) ||
                    Boolean(opts.bymonthday) ||
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["notEmpty"])(opts.bymonthday) ||
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byweekday) ||
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byeaster))) {
                    switch (opts.freq) {
                        case _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].YEARLY:
                            if (!opts.bymonth)
                                opts.bymonth = opts.dtstart.getUTCMonth() + 1;
                            opts.bymonthday = opts.dtstart.getUTCDate();
                            break;
                        case _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MONTHLY:
                            opts.bymonthday = opts.dtstart.getUTCDate();
                            break;
                        case _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].WEEKLY:
                            opts.byweekday = [_dateutil__WEBPACK_IMPORTED_MODULE_4__["default"].getWeekday(opts.dtstart)];
                            break;
                    }
                }
                // bymonth
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.bymonth) && !Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(opts.bymonth)) {
                    opts.bymonth = [opts.bymonth];
                }
                // byyearday
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byyearday) &&
                    !Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(opts.byyearday) &&
                    Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.byyearday)) {
                    opts.byyearday = [opts.byyearday];
                }
                // bymonthday
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.bymonthday)) {
                    opts.bymonthday = [];
                    opts.bynmonthday = [];
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(opts.bymonthday)) {
                    var bymonthday = [];
                    var bynmonthday = [];
                    for (var i = 0; i < opts.bymonthday.length; i++) {
                        var v = opts.bymonthday[i];
                        if (v > 0) {
                            bymonthday.push(v);
                        }
                        else if (v < 0) {
                            bynmonthday.push(v);
                        }
                    }
                    opts.bymonthday = bymonthday;
                    opts.bynmonthday = bynmonthday;
                }
                else if (opts.bymonthday < 0) {
                    opts.bynmonthday = [opts.bymonthday];
                    opts.bymonthday = [];
                }
                else {
                    opts.bynmonthday = [];
                    opts.bymonthday = [opts.bymonthday];
                }
                // byweekno
                if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byweekno) && !Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isArray"])(opts.byweekno)) {
                    opts.byweekno = [opts.byweekno];
                }
                // byweekday / bynweekday
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byweekday)) {
                    opts.bynweekday = null;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.byweekday)) {
                    opts.byweekday = [opts.byweekday];
                    opts.bynweekday = null;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isWeekdayStr"])(opts.byweekday)) {
                    opts.byweekday = [_weekday__WEBPACK_IMPORTED_MODULE_5__["Weekday"].fromStr(opts.byweekday).weekday];
                    opts.bynweekday = null;
                }
                else if (opts.byweekday instanceof _weekday__WEBPACK_IMPORTED_MODULE_5__["Weekday"]) {
                    if (!opts.byweekday.n || opts.freq > _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MONTHLY) {
                        opts.byweekday = [opts.byweekday.weekday];
                        opts.bynweekday = null;
                    }
                    else {
                        opts.bynweekday = [[opts.byweekday.weekday, opts.byweekday.n]];
                        opts.byweekday = null;
                    }
                }
                else {
                    var byweekday = [];
                    var bynweekday = [];
                    for (var i = 0; i < opts.byweekday.length; i++) {
                        var wday = opts.byweekday[i];
                        if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(wday)) {
                            byweekday.push(wday);
                            continue;
                        }
                        else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isWeekdayStr"])(wday)) {
                            byweekday.push(_weekday__WEBPACK_IMPORTED_MODULE_5__["Weekday"].fromStr(wday).weekday);
                            continue;
                        }
                        if (!wday.n || opts.freq > _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MONTHLY) {
                            byweekday.push(wday.weekday);
                        }
                        else {
                            bynweekday.push([wday.weekday, wday.n]);
                        }
                    }
                    opts.byweekday = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["notEmpty"])(byweekday) ? byweekday : null;
                    opts.bynweekday = Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["notEmpty"])(bynweekday) ? bynweekday : null;
                }
                // byhour
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byhour)) {
                    opts.byhour = opts.freq < _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].HOURLY ? [opts.dtstart.getUTCHours()] : null;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.byhour)) {
                    opts.byhour = [opts.byhour];
                }
                // byminute
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.byminute)) {
                    opts.byminute =
                        opts.freq < _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].MINUTELY ? [opts.dtstart.getUTCMinutes()] : null;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.byminute)) {
                    opts.byminute = [opts.byminute];
                }
                // bysecond
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isPresent"])(opts.bysecond)) {
                    opts.bysecond =
                        opts.freq < _rrule__WEBPACK_IMPORTED_MODULE_3__["RRule"].SECONDLY ? [opts.dtstart.getUTCSeconds()] : null;
                }
                else if (Object(_helpers__WEBPACK_IMPORTED_MODULE_2__["isNumber"])(opts.bysecond)) {
                    opts.bysecond = [opts.bysecond];
                }
                return { parsedOptions: opts };
            }
            function buildTimeset(opts) {
                var millisecondModulo = opts.dtstart.getTime() % 1000;
                if (!Object(_types__WEBPACK_IMPORTED_MODULE_1__["freqIsDailyOrGreater"])(opts.freq)) {
                    return [];
                }
                var timeset = [];
                opts.byhour.forEach(function (hour) {
                    opts.byminute.forEach(function (minute) {
                        opts.bysecond.forEach(function (second) {
                            timeset.push(new _datetime__WEBPACK_IMPORTED_MODULE_6__["Time"](hour, minute, second, millisecondModulo));
                        });
                    });
                });
                return timeset;
            }
//# sourceMappingURL=parseoptions.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/parsestring.js":
        /*!****************************************************!*\
  !*** ./node_modules/rrule/dist/esm/parsestring.js ***!
  \****************************************************/
        /*! exports provided: parseString, parseDtstart */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseString", function() { return parseString; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseDtstart", function() { return parseDtstart; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");





            function parseString(rfcString) {
                var options = rfcString
                    .split('\n')
                    .map(parseLine)
                    .filter(function (x) { return x !== null; });
                return Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({}, options[0]), options[1]);
            }
            function parseDtstart(line) {
                var options = {};
                var dtstartWithZone = /DTSTART(?:;TZID=([^:=]+?))?(?::|=)([^;\s]+)/i.exec(line);
                if (!dtstartWithZone) {
                    return options;
                }
                var tzid = dtstartWithZone[1], dtstart = dtstartWithZone[2];
                if (tzid) {
                    options.tzid = tzid;
                }
                options.dtstart = _dateutil__WEBPACK_IMPORTED_MODULE_3__["default"].untilStringToDate(dtstart);
                return options;
            }
            function parseLine(rfcString) {
                rfcString = rfcString.replace(/^\s+|\s+$/, '');
                if (!rfcString.length)
                    return null;
                var header = /^([A-Z]+?)[:;]/.exec(rfcString.toUpperCase());
                if (!header) {
                    return parseRrule(rfcString);
                }
                var key = header[1];
                switch (key.toUpperCase()) {
                    case 'RRULE':
                    case 'EXRULE':
                        return parseRrule(rfcString);
                    case 'DTSTART':
                        return parseDtstart(rfcString);
                    default:
                        throw new Error("Unsupported RFC prop ".concat(key, " in ").concat(rfcString));
                }
            }
            function parseRrule(line) {
                var strippedLine = line.replace(/^RRULE:/i, '');
                var options = parseDtstart(strippedLine);
                var attrs = line.replace(/^(?:RRULE|EXRULE):/i, '').split(';');
                attrs.forEach(function (attr) {
                    var _a = attr.split('='), key = _a[0], value = _a[1];
                    switch (key.toUpperCase()) {
                        case 'FREQ':
                            options.freq = _types__WEBPACK_IMPORTED_MODULE_1__["Frequency"][value.toUpperCase()];
                            break;
                        case 'WKST':
                            options.wkst = _rrule__WEBPACK_IMPORTED_MODULE_4__["Days"][value.toUpperCase()];
                            break;
                        case 'COUNT':
                        case 'INTERVAL':
                        case 'BYSETPOS':
                        case 'BYMONTH':
                        case 'BYMONTHDAY':
                        case 'BYYEARDAY':
                        case 'BYWEEKNO':
                        case 'BYHOUR':
                        case 'BYMINUTE':
                        case 'BYSECOND':
                            var num = parseNumber(value);
                            var optionKey = key.toLowerCase();
                            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                            // @ts-ignore
                            options[optionKey] = num;
                            break;
                        case 'BYWEEKDAY':
                        case 'BYDAY':
                            options.byweekday = parseWeekday(value);
                            break;
                        case 'DTSTART':
                        case 'TZID':
                            // for backwards compatibility
                            var dtstart = parseDtstart(line);
                            options.tzid = dtstart.tzid;
                            options.dtstart = dtstart.dtstart;
                            break;
                        case 'UNTIL':
                            options.until = _dateutil__WEBPACK_IMPORTED_MODULE_3__["default"].untilStringToDate(value);
                            break;
                        case 'BYEASTER':
                            options.byeaster = Number(value);
                            break;
                        default:
                            throw new Error("Unknown RRULE property '" + key + "'");
                    }
                });
                return options;
            }
            function parseNumber(value) {
                if (value.indexOf(',') !== -1) {
                    var values = value.split(',');
                    return values.map(parseIndividualNumber);
                }
                return parseIndividualNumber(value);
            }
            function parseIndividualNumber(value) {
                if (/^[+-]?\d+$/.test(value)) {
                    return Number(value);
                }
                return value;
            }
            function parseWeekday(value) {
                var days = value.split(',');
                return days.map(function (day) {
                    if (day.length === 2) {
                        // MO, TU, ...
                        return _rrule__WEBPACK_IMPORTED_MODULE_4__["Days"][day]; // wday instanceof Weekday
                    }
                    // -1MO, +3FR, 1SO, 13TU ...
                    var parts = day.match(/^([+-]?\d{1,2})([A-Z]{2})$/);
                    if (!parts || parts.length < 3) {
                        throw new SyntaxError("Invalid weekday string: ".concat(day));
                    }
                    var n = Number(parts[1]);
                    var wdaypart = parts[2];
                    var wday = _rrule__WEBPACK_IMPORTED_MODULE_4__["Days"][wdaypart].weekday;
                    return new _weekday__WEBPACK_IMPORTED_MODULE_2__["Weekday"](wday, n);
                });
            }
//# sourceMappingURL=parsestring.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/rrule.js":
        /*!**********************************************!*\
  !*** ./node_modules/rrule/dist/esm/rrule.js ***!
  \**********************************************/
        /*! exports provided: Days, DEFAULT_OPTIONS, defaultKeys, RRule */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Days", function() { return Days; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DEFAULT_OPTIONS", function() { return DEFAULT_OPTIONS; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "defaultKeys", function() { return defaultKeys; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RRule", function() { return RRule; });
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _iterresult__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./iterresult */ "./node_modules/rrule/dist/esm/iterresult.js");
            /* harmony import */ var _callbackiterresult__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./callbackiterresult */ "./node_modules/rrule/dist/esm/callbackiterresult.js");
            /* harmony import */ var _nlp_index__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./nlp/index */ "./node_modules/rrule/dist/esm/nlp/index.js");
            /* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./types */ "./node_modules/rrule/dist/esm/types.js");
            /* harmony import */ var _parseoptions__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./parseoptions */ "./node_modules/rrule/dist/esm/parseoptions.js");
            /* harmony import */ var _parsestring__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./parsestring */ "./node_modules/rrule/dist/esm/parsestring.js");
            /* harmony import */ var _optionstostring__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./optionstostring */ "./node_modules/rrule/dist/esm/optionstostring.js");
            /* harmony import */ var _cache__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./cache */ "./node_modules/rrule/dist/esm/cache.js");
            /* harmony import */ var _weekday__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./weekday */ "./node_modules/rrule/dist/esm/weekday.js");
            /* harmony import */ var _iter_index__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./iter/index */ "./node_modules/rrule/dist/esm/iter/index.js");











// =============================================================================
// RRule
// =============================================================================
            var Days = {
                MO: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](0),
                TU: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](1),
                WE: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](2),
                TH: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](3),
                FR: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](4),
                SA: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](5),
                SU: new _weekday__WEBPACK_IMPORTED_MODULE_9__["Weekday"](6),
            };
            var DEFAULT_OPTIONS = {
                freq: _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].YEARLY,
                dtstart: null,
                interval: 1,
                wkst: Days.MO,
                count: null,
                until: null,
                tzid: null,
                bysetpos: null,
                bymonth: null,
                bymonthday: null,
                bynmonthday: null,
                byyearday: null,
                byweekno: null,
                byweekday: null,
                bynweekday: null,
                byhour: null,
                byminute: null,
                bysecond: null,
                byeaster: null,
            };
            var defaultKeys = Object.keys(DEFAULT_OPTIONS);
            /**
             *
             * @param {Options?} options - see <http://labix.org/python-dateutil/#head-cf004ee9a75592797e076752b2a889c10f445418>
             * - The only required option is `freq`, one of RRule.YEARLY, RRule.MONTHLY, ...
             * @constructor
             */
            var RRule = /** @class */ (function () {
                function RRule(options, noCache) {
                    if (options === void 0) { options = {}; }
                    if (noCache === void 0) { noCache = false; }
                    // RFC string
                    this._cache = noCache ? null : new _cache__WEBPACK_IMPORTED_MODULE_8__["Cache"]();
                    // used by toString()
                    this.origOptions = Object(_parseoptions__WEBPACK_IMPORTED_MODULE_5__["initializeOptions"])(options);
                    var parsedOptions = Object(_parseoptions__WEBPACK_IMPORTED_MODULE_5__["parseOptions"])(options).parsedOptions;
                    this.options = parsedOptions;
                }
                RRule.parseText = function (text, language) {
                    return Object(_nlp_index__WEBPACK_IMPORTED_MODULE_3__["parseText"])(text, language);
                };
                RRule.fromText = function (text, language) {
                    return Object(_nlp_index__WEBPACK_IMPORTED_MODULE_3__["fromText"])(text, language);
                };
                RRule.fromString = function (str) {
                    return new RRule(RRule.parseString(str) || undefined);
                };
                RRule.prototype._iter = function (iterResult) {
                    return Object(_iter_index__WEBPACK_IMPORTED_MODULE_10__["iter"])(iterResult, this.options);
                };
                RRule.prototype._cacheGet = function (what, args) {
                    if (!this._cache)
                        return false;
                    return this._cache._cacheGet(what, args);
                };
                RRule.prototype._cacheAdd = function (what, value, args) {
                    if (!this._cache)
                        return;
                    return this._cache._cacheAdd(what, value, args);
                };
                /**
                 * @param {Function} iterator - optional function that will be called
                 * on each date that is added. It can return false
                 * to stop the iteration.
                 * @return Array containing all recurrences.
                 */
                RRule.prototype.all = function (iterator) {
                    if (iterator) {
                        return this._iter(new _callbackiterresult__WEBPACK_IMPORTED_MODULE_2__["default"]('all', {}, iterator));
                    }
                    var result = this._cacheGet('all');
                    if (result === false) {
                        result = this._iter(new _iterresult__WEBPACK_IMPORTED_MODULE_1__["default"]('all', {}));
                        this._cacheAdd('all', result);
                    }
                    return result;
                };
                /**
                 * Returns all the occurrences of the rrule between after and before.
                 * The inc keyword defines what happens if after and/or before are
                 * themselves occurrences. With inc == True, they will be included in the
                 * list, if they are found in the recurrence set.
                 *
                 * @return Array
                 */
                RRule.prototype.between = function (after, before, inc, iterator) {
                    if (inc === void 0) { inc = false; }
                    if (!_dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].isValidDate(after) || !_dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].isValidDate(before)) {
                        throw new Error('Invalid date passed in to RRule.between');
                    }
                    var args = {
                        before: before,
                        after: after,
                        inc: inc,
                    };
                    if (iterator) {
                        return this._iter(new _callbackiterresult__WEBPACK_IMPORTED_MODULE_2__["default"]('between', args, iterator));
                    }
                    var result = this._cacheGet('between', args);
                    if (result === false) {
                        result = this._iter(new _iterresult__WEBPACK_IMPORTED_MODULE_1__["default"]('between', args));
                        this._cacheAdd('between', result, args);
                    }
                    return result;
                };
                /**
                 * Returns the last recurrence before the given datetime instance.
                 * The inc keyword defines what happens if dt is an occurrence.
                 * With inc == True, if dt itself is an occurrence, it will be returned.
                 *
                 * @return Date or null
                 */
                RRule.prototype.before = function (dt, inc) {
                    if (inc === void 0) { inc = false; }
                    if (!_dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].isValidDate(dt)) {
                        throw new Error('Invalid date passed in to RRule.before');
                    }
                    var args = { dt: dt, inc: inc };
                    var result = this._cacheGet('before', args);
                    if (result === false) {
                        result = this._iter(new _iterresult__WEBPACK_IMPORTED_MODULE_1__["default"]('before', args));
                        this._cacheAdd('before', result, args);
                    }
                    return result;
                };
                /**
                 * Returns the first recurrence after the given datetime instance.
                 * The inc keyword defines what happens if dt is an occurrence.
                 * With inc == True, if dt itself is an occurrence, it will be returned.
                 *
                 * @return Date or null
                 */
                RRule.prototype.after = function (dt, inc) {
                    if (inc === void 0) { inc = false; }
                    if (!_dateutil__WEBPACK_IMPORTED_MODULE_0__["default"].isValidDate(dt)) {
                        throw new Error('Invalid date passed in to RRule.after');
                    }
                    var args = { dt: dt, inc: inc };
                    var result = this._cacheGet('after', args);
                    if (result === false) {
                        result = this._iter(new _iterresult__WEBPACK_IMPORTED_MODULE_1__["default"]('after', args));
                        this._cacheAdd('after', result, args);
                    }
                    return result;
                };
                /**
                 * Returns the number of recurrences in this set. It will have go trough
                 * the whole recurrence, if this hasn't been done before.
                 */
                RRule.prototype.count = function () {
                    return this.all().length;
                };
                /**
                 * Converts the rrule into its string representation
                 *
                 * @see <http://www.ietf.org/rfc/rfc2445.txt>
                 * @return String
                 */
                RRule.prototype.toString = function () {
                    return Object(_optionstostring__WEBPACK_IMPORTED_MODULE_7__["optionsToString"])(this.origOptions);
                };
                /**
                 * Will convert all rules described in nlp:ToText
                 * to text.
                 */
                RRule.prototype.toText = function (gettext, language, dateFormatter) {
                    return Object(_nlp_index__WEBPACK_IMPORTED_MODULE_3__["toText"])(this, gettext, language, dateFormatter);
                };
                RRule.prototype.isFullyConvertibleToText = function () {
                    return Object(_nlp_index__WEBPACK_IMPORTED_MODULE_3__["isFullyConvertible"])(this);
                };
                /**
                 * @return a RRule instance with the same freq and options
                 * as this one (cache is not cloned)
                 */
                RRule.prototype.clone = function () {
                    return new RRule(this.origOptions);
                };
                // RRule class 'constants'
                RRule.FREQUENCIES = [
                    'YEARLY',
                    'MONTHLY',
                    'WEEKLY',
                    'DAILY',
                    'HOURLY',
                    'MINUTELY',
                    'SECONDLY',
                ];
                RRule.YEARLY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].YEARLY;
                RRule.MONTHLY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].MONTHLY;
                RRule.WEEKLY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].WEEKLY;
                RRule.DAILY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].DAILY;
                RRule.HOURLY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].HOURLY;
                RRule.MINUTELY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].MINUTELY;
                RRule.SECONDLY = _types__WEBPACK_IMPORTED_MODULE_4__["Frequency"].SECONDLY;
                RRule.MO = Days.MO;
                RRule.TU = Days.TU;
                RRule.WE = Days.WE;
                RRule.TH = Days.TH;
                RRule.FR = Days.FR;
                RRule.SA = Days.SA;
                RRule.SU = Days.SU;
                RRule.parseString = _parsestring__WEBPACK_IMPORTED_MODULE_6__["parseString"];
                RRule.optionsToString = _optionstostring__WEBPACK_IMPORTED_MODULE_7__["optionsToString"];
                return RRule;
            }());

//# sourceMappingURL=rrule.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/rruleset.js":
        /*!*************************************************!*\
  !*** ./node_modules/rrule/dist/esm/rruleset.js ***!
  \*************************************************/
        /*! exports provided: RRuleSet */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RRuleSet", function() { return RRuleSet; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _iterset__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./iterset */ "./node_modules/rrule/dist/esm/iterset.js");
            /* harmony import */ var _rrulestr__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./rrulestr */ "./node_modules/rrule/dist/esm/rrulestr.js");
            /* harmony import */ var _optionstostring__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./optionstostring */ "./node_modules/rrule/dist/esm/optionstostring.js");







            function createGetterSetter(fieldName) {
                var _this = this;
                return function (field) {
                    if (field !== undefined) {
                        _this["_".concat(fieldName)] = field;
                    }
                    if (_this["_".concat(fieldName)] !== undefined) {
                        return _this["_".concat(fieldName)];
                    }
                    for (var i = 0; i < _this._rrule.length; i++) {
                        var field_1 = _this._rrule[i].origOptions[fieldName];
                        if (field_1) {
                            return field_1;
                        }
                    }
                };
            }
            var RRuleSet = /** @class */ (function (_super) {
                Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"])(RRuleSet, _super);
                /**
                 *
                 * @param {Boolean?} noCache
                 * The same stratagy as RRule on cache, default to false
                 * @constructor
                 */
                function RRuleSet(noCache) {
                    if (noCache === void 0) { noCache = false; }
                    var _this = _super.call(this, {}, noCache) || this;
                    _this.dtstart = createGetterSetter.apply(_this, ['dtstart']);
                    _this.tzid = createGetterSetter.apply(_this, ['tzid']);
                    _this._rrule = [];
                    _this._rdate = [];
                    _this._exrule = [];
                    _this._exdate = [];
                    return _this;
                }
                RRuleSet.prototype._iter = function (iterResult) {
                    return Object(_iterset__WEBPACK_IMPORTED_MODULE_4__["iterSet"])(iterResult, this._rrule, this._exrule, this._rdate, this._exdate, this.tzid());
                };
                /**
                 * Adds an RRule to the set
                 *
                 * @param {RRule}
                 */
                RRuleSet.prototype.rrule = function (rrule) {
                    _addRule(rrule, this._rrule);
                };
                /**
                 * Adds an EXRULE to the set
                 *
                 * @param {RRule}
                 */
                RRuleSet.prototype.exrule = function (rrule) {
                    _addRule(rrule, this._exrule);
                };
                /**
                 * Adds an RDate to the set
                 *
                 * @param {Date}
                 */
                RRuleSet.prototype.rdate = function (date) {
                    _addDate(date, this._rdate);
                };
                /**
                 * Adds an EXDATE to the set
                 *
                 * @param {Date}
                 */
                RRuleSet.prototype.exdate = function (date) {
                    _addDate(date, this._exdate);
                };
                /**
                 * Get list of included rrules in this recurrence set.
                 *
                 * @return List of rrules
                 */
                RRuleSet.prototype.rrules = function () {
                    return this._rrule.map(function (e) { return Object(_rrulestr__WEBPACK_IMPORTED_MODULE_5__["rrulestr"])(e.toString()); });
                };
                /**
                 * Get list of excluded rrules in this recurrence set.
                 *
                 * @return List of exrules
                 */
                RRuleSet.prototype.exrules = function () {
                    return this._exrule.map(function (e) { return Object(_rrulestr__WEBPACK_IMPORTED_MODULE_5__["rrulestr"])(e.toString()); });
                };
                /**
                 * Get list of included datetimes in this recurrence set.
                 *
                 * @return List of rdates
                 */
                RRuleSet.prototype.rdates = function () {
                    return this._rdate.map(function (e) { return new Date(e.getTime()); });
                };
                /**
                 * Get list of included datetimes in this recurrence set.
                 *
                 * @return List of exdates
                 */
                RRuleSet.prototype.exdates = function () {
                    return this._exdate.map(function (e) { return new Date(e.getTime()); });
                };
                RRuleSet.prototype.valueOf = function () {
                    var result = [];
                    if (!this._rrule.length && this._dtstart) {
                        result = result.concat(Object(_optionstostring__WEBPACK_IMPORTED_MODULE_6__["optionsToString"])({ dtstart: this._dtstart }));
                    }
                    this._rrule.forEach(function (rrule) {
                        result = result.concat(rrule.toString().split('\n'));
                    });
                    this._exrule.forEach(function (exrule) {
                        result = result.concat(exrule
                            .toString()
                            .split('\n')
                            .map(function (line) { return line.replace(/^RRULE:/, 'EXRULE:'); })
                            .filter(function (line) { return !/^DTSTART/.test(line); }));
                    });
                    if (this._rdate.length) {
                        result.push(rdatesToString('RDATE', this._rdate, this.tzid()));
                    }
                    if (this._exdate.length) {
                        result.push(rdatesToString('EXDATE', this._exdate, this.tzid()));
                    }
                    return result;
                };
                /**
                 * to generate recurrence field such as:
                 * DTSTART:19970902T010000Z
                 * RRULE:FREQ=YEARLY;COUNT=2;BYDAY=TU
                 * RRULE:FREQ=YEARLY;COUNT=1;BYDAY=TH
                 */
                RRuleSet.prototype.toString = function () {
                    return this.valueOf().join('\n');
                };
                /**
                 * Create a new RRuleSet Object completely base on current instance
                 */
                RRuleSet.prototype.clone = function () {
                    var rrs = new RRuleSet(!!this._cache);
                    this._rrule.forEach(function (rule) { return rrs.rrule(rule.clone()); });
                    this._exrule.forEach(function (rule) { return rrs.exrule(rule.clone()); });
                    this._rdate.forEach(function (date) { return rrs.rdate(new Date(date.getTime())); });
                    this._exdate.forEach(function (date) { return rrs.exdate(new Date(date.getTime())); });
                    return rrs;
                };
                return RRuleSet;
            }(_rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"]));

            function _addRule(rrule, collection) {
                if (!(rrule instanceof _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"])) {
                    throw new TypeError(String(rrule) + ' is not RRule instance');
                }
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_3__["includes"])(collection.map(String), String(rrule))) {
                    collection.push(rrule);
                }
            }
            function _addDate(date, collection) {
                if (!(date instanceof Date)) {
                    throw new TypeError(String(date) + ' is not Date instance');
                }
                if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_3__["includes"])(collection.map(Number), Number(date))) {
                    collection.push(date);
                    _dateutil__WEBPACK_IMPORTED_MODULE_2__["default"].sort(collection);
                }
            }
            function rdatesToString(param, rdates, tzid) {
                var isUTC = !tzid || tzid.toUpperCase() === 'UTC';
                var header = isUTC ? "".concat(param, ":") : "".concat(param, ";TZID=").concat(tzid, ":");
                var dateString = rdates
                    .map(function (rdate) { return _dateutil__WEBPACK_IMPORTED_MODULE_2__["default"].timeToUntilString(rdate.valueOf(), isUTC); })
                    .join(',');
                return "".concat(header).concat(dateString);
            }
//# sourceMappingURL=rruleset.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/rrulestr.js":
        /*!*************************************************!*\
  !*** ./node_modules/rrule/dist/esm/rrulestr.js ***!
  \*************************************************/
        /*! exports provided: parseInput, rrulestr */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseInput", function() { return parseInput; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "rrulestr", function() { return rrulestr; });
            /* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/rrule/node_modules/tslib/tslib.es6.js");
            /* harmony import */ var _rrule__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./rrule */ "./node_modules/rrule/dist/esm/rrule.js");
            /* harmony import */ var _rruleset__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./rruleset */ "./node_modules/rrule/dist/esm/rruleset.js");
            /* harmony import */ var _dateutil__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./dateutil */ "./node_modules/rrule/dist/esm/dateutil.js");
            /* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./helpers */ "./node_modules/rrule/dist/esm/helpers.js");
            /* harmony import */ var _parsestring__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./parsestring */ "./node_modules/rrule/dist/esm/parsestring.js");






            /**
             * RRuleStr
             * To parse a set of rrule strings
             */
            var DEFAULT_OPTIONS = {
                dtstart: null,
                cache: false,
                unfold: false,
                forceset: false,
                compatible: false,
                tzid: null,
            };
            function parseInput(s, options) {
                var rrulevals = [];
                var rdatevals = [];
                var exrulevals = [];
                var exdatevals = [];
                var parsedDtstart = Object(_parsestring__WEBPACK_IMPORTED_MODULE_5__["parseDtstart"])(s);
                var dtstart = parsedDtstart.dtstart;
                var tzid = parsedDtstart.tzid;
                var lines = splitIntoLines(s, options.unfold);
                lines.forEach(function (line) {
                    var _a;
                    if (!line)
                        return;
                    var _b = breakDownLine(line), name = _b.name, parms = _b.parms, value = _b.value;
                    switch (name.toUpperCase()) {
                        case 'RRULE':
                            if (parms.length) {
                                throw new Error("unsupported RRULE parm: ".concat(parms.join(',')));
                            }
                            rrulevals.push(Object(_parsestring__WEBPACK_IMPORTED_MODULE_5__["parseString"])(line));
                            break;
                        case 'RDATE':
                            var _c = (_a = /RDATE(?:;TZID=([^:=]+))?/i.exec(line)) !== null && _a !== void 0 ? _a : [], rdateTzid = _c[1];
                            if (rdateTzid && !tzid) {
                                tzid = rdateTzid;
                            }
                            rdatevals = rdatevals.concat(parseRDate(value, parms));
                            break;
                        case 'EXRULE':
                            if (parms.length) {
                                throw new Error("unsupported EXRULE parm: ".concat(parms.join(',')));
                            }
                            exrulevals.push(Object(_parsestring__WEBPACK_IMPORTED_MODULE_5__["parseString"])(value));
                            break;
                        case 'EXDATE':
                            exdatevals = exdatevals.concat(parseRDate(value, parms));
                            break;
                        case 'DTSTART':
                            break;
                        default:
                            throw new Error('unsupported property: ' + name);
                    }
                });
                return {
                    dtstart: dtstart,
                    tzid: tzid,
                    rrulevals: rrulevals,
                    rdatevals: rdatevals,
                    exrulevals: exrulevals,
                    exdatevals: exdatevals,
                };
            }
            function buildRule(s, options) {
                var _a = parseInput(s, options), rrulevals = _a.rrulevals, rdatevals = _a.rdatevals, exrulevals = _a.exrulevals, exdatevals = _a.exdatevals, dtstart = _a.dtstart, tzid = _a.tzid;
                var noCache = options.cache === false;
                if (options.compatible) {
                    options.forceset = true;
                    options.unfold = true;
                }
                if (options.forceset ||
                    rrulevals.length > 1 ||
                    rdatevals.length ||
                    exrulevals.length ||
                    exdatevals.length) {
                    var rset_1 = new _rruleset__WEBPACK_IMPORTED_MODULE_2__["RRuleSet"](noCache);
                    rset_1.dtstart(dtstart);
                    rset_1.tzid(tzid || undefined);
                    rrulevals.forEach(function (val) {
                        rset_1.rrule(new _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"](groomRruleOptions(val, dtstart, tzid), noCache));
                    });
                    rdatevals.forEach(function (date) {
                        rset_1.rdate(date);
                    });
                    exrulevals.forEach(function (val) {
                        rset_1.exrule(new _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"](groomRruleOptions(val, dtstart, tzid), noCache));
                    });
                    exdatevals.forEach(function (date) {
                        rset_1.exdate(date);
                    });
                    if (options.compatible && options.dtstart)
                        rset_1.rdate(dtstart);
                    return rset_1;
                }
                var val = rrulevals[0] || {};
                return new _rrule__WEBPACK_IMPORTED_MODULE_1__["RRule"](groomRruleOptions(val, val.dtstart || options.dtstart || dtstart, val.tzid || options.tzid || tzid), noCache);
            }
            function rrulestr(s, options) {
                if (options === void 0) { options = {}; }
                return buildRule(s, initializeOptions(options));
            }
            function groomRruleOptions(val, dtstart, tzid) {
                return Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({}, val), { dtstart: dtstart, tzid: tzid });
            }
            function initializeOptions(options) {
                var invalid = [];
                var keys = Object.keys(options);
                var defaultKeys = Object.keys(DEFAULT_OPTIONS);
                keys.forEach(function (key) {
                    if (!Object(_helpers__WEBPACK_IMPORTED_MODULE_4__["includes"])(defaultKeys, key))
                        invalid.push(key);
                });
                if (invalid.length) {
                    throw new Error('Invalid options: ' + invalid.join(', '));
                }
                return Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])(Object(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"])({}, DEFAULT_OPTIONS), options);
            }
            function extractName(line) {
                if (line.indexOf(':') === -1) {
                    return {
                        name: 'RRULE',
                        value: line,
                    };
                }
                var _a = Object(_helpers__WEBPACK_IMPORTED_MODULE_4__["split"])(line, ':', 1), name = _a[0], value = _a[1];
                return {
                    name: name,
                    value: value,
                };
            }
            function breakDownLine(line) {
                var _a = extractName(line), name = _a.name, value = _a.value;
                var parms = name.split(';');
                if (!parms)
                    throw new Error('empty property name');
                return {
                    name: parms[0].toUpperCase(),
                    parms: parms.slice(1),
                    value: value,
                };
            }
            function splitIntoLines(s, unfold) {
                if (unfold === void 0) { unfold = false; }
                s = s && s.trim();
                if (!s)
                    throw new Error('Invalid empty string');
                // More info about 'unfold' option
                // Go head to http://www.ietf.org/rfc/rfc2445.txt
                if (!unfold) {
                    return s.split(/\s/);
                }
                var lines = s.split('\n');
                var i = 0;
                while (i < lines.length) {
                    // TODO
                    var line = (lines[i] = lines[i].replace(/\s+$/g, ''));
                    if (!line) {
                        lines.splice(i, 1);
                    }
                    else if (i > 0 && line[0] === ' ') {
                        lines[i - 1] += line.slice(1);
                        lines.splice(i, 1);
                    }
                    else {
                        i += 1;
                    }
                }
                return lines;
            }
            function validateDateParm(parms) {
                parms.forEach(function (parm) {
                    if (!/(VALUE=DATE(-TIME)?)|(TZID=)/.test(parm)) {
                        throw new Error('unsupported RDATE/EXDATE parm: ' + parm);
                    }
                });
            }
            function parseRDate(rdateval, parms) {
                validateDateParm(parms);
                return rdateval
                    .split(',')
                    .map(function (datestr) { return _dateutil__WEBPACK_IMPORTED_MODULE_3__["default"].untilStringToDate(datestr); });
            }
//# sourceMappingURL=rrulestr.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/types.js":
        /*!**********************************************!*\
  !*** ./node_modules/rrule/dist/esm/types.js ***!
  \**********************************************/
        /*! exports provided: Frequency, freqIsDailyOrGreater */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Frequency", function() { return Frequency; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "freqIsDailyOrGreater", function() { return freqIsDailyOrGreater; });
            var Frequency;
            (function (Frequency) {
                Frequency[Frequency["YEARLY"] = 0] = "YEARLY";
                Frequency[Frequency["MONTHLY"] = 1] = "MONTHLY";
                Frequency[Frequency["WEEKLY"] = 2] = "WEEKLY";
                Frequency[Frequency["DAILY"] = 3] = "DAILY";
                Frequency[Frequency["HOURLY"] = 4] = "HOURLY";
                Frequency[Frequency["MINUTELY"] = 5] = "MINUTELY";
                Frequency[Frequency["SECONDLY"] = 6] = "SECONDLY";
            })(Frequency || (Frequency = {}));
            function freqIsDailyOrGreater(freq) {
                return freq < Frequency.HOURLY;
            }
//# sourceMappingURL=types.js.map

            /***/ }),

        /***/ "./node_modules/rrule/dist/esm/weekday.js":
        /*!************************************************!*\
  !*** ./node_modules/rrule/dist/esm/weekday.js ***!
  \************************************************/
        /*! exports provided: ALL_WEEKDAYS, Weekday */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ALL_WEEKDAYS", function() { return ALL_WEEKDAYS; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Weekday", function() { return Weekday; });
// =============================================================================
// Weekday
// =============================================================================
            var ALL_WEEKDAYS = [
                'MO',
                'TU',
                'WE',
                'TH',
                'FR',
                'SA',
                'SU',
            ];
            var Weekday = /** @class */ (function () {
                function Weekday(weekday, n) {
                    if (n === 0)
                        throw new Error("Can't create weekday with n == 0");
                    this.weekday = weekday;
                    this.n = n;
                }
                Weekday.fromStr = function (str) {
                    return new Weekday(ALL_WEEKDAYS.indexOf(str));
                };
                // __call__ - Cannot call the object directly, do it through
                // e.g. RRule.TH.nth(-1) instead,
                Weekday.prototype.nth = function (n) {
                    return this.n === n ? this : new Weekday(this.weekday, n);
                };
                // __eq__
                Weekday.prototype.equals = function (other) {
                    return this.weekday === other.weekday && this.n === other.n;
                };
                // __repr__
                Weekday.prototype.toString = function () {
                    var s = ALL_WEEKDAYS[this.weekday];
                    if (this.n)
                        s = (this.n > 0 ? '+' : '') + String(this.n) + s;
                    return s;
                };
                Weekday.prototype.getJsWeekday = function () {
                    return this.weekday === 6 ? 0 : this.weekday + 1;
                };
                return Weekday;
            }());

//# sourceMappingURL=weekday.js.map

            /***/ }),

        /***/ "./node_modules/rrule/node_modules/tslib/tslib.es6.js":
        /*!************************************************************!*\
  !*** ./node_modules/rrule/node_modules/tslib/tslib.es6.js ***!
  \************************************************************/
        /*! exports provided: __extends, __assign, __rest, __decorate, __param, __metadata, __awaiter, __generator, __createBinding, __exportStar, __values, __read, __spread, __spreadArrays, __spreadArray, __await, __asyncGenerator, __asyncDelegator, __asyncValues, __makeTemplateObject, __importStar, __importDefault, __classPrivateFieldGet, __classPrivateFieldSet, __classPrivateFieldIn */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__extends", function() { return __extends; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__assign", function() { return __assign; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__rest", function() { return __rest; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__decorate", function() { return __decorate; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__param", function() { return __param; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__metadata", function() { return __metadata; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__awaiter", function() { return __awaiter; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__generator", function() { return __generator; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__createBinding", function() { return __createBinding; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__exportStar", function() { return __exportStar; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__values", function() { return __values; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__read", function() { return __read; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__spread", function() { return __spread; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__spreadArrays", function() { return __spreadArrays; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__spreadArray", function() { return __spreadArray; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__await", function() { return __await; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncGenerator", function() { return __asyncGenerator; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncDelegator", function() { return __asyncDelegator; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncValues", function() { return __asyncValues; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__makeTemplateObject", function() { return __makeTemplateObject; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__importStar", function() { return __importStar; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__importDefault", function() { return __importDefault; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__classPrivateFieldGet", function() { return __classPrivateFieldGet; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__classPrivateFieldSet", function() { return __classPrivateFieldSet; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__classPrivateFieldIn", function() { return __classPrivateFieldIn; });
            /******************************************************************************
             Copyright (c) Microsoft Corporation.

             Permission to use, copy, modify, and/or distribute this software for any
             purpose with or without fee is hereby granted.

             THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
             REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
             AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
             INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
             LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
             OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
             PERFORMANCE OF THIS SOFTWARE.
             ***************************************************************************** */
            /* global Reflect, Promise */

            var extendStatics = function(d, b) {
                extendStatics = Object.setPrototypeOf ||
                    ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                    function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
                return extendStatics(d, b);
            };

            function __extends(d, b) {
                if (typeof b !== "function" && b !== null)
                    throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
                extendStatics(d, b);
                function __() { this.constructor = d; }
                d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
            }

            var __assign = function() {
                __assign = Object.assign || function __assign(t) {
                    for (var s, i = 1, n = arguments.length; i < n; i++) {
                        s = arguments[i];
                        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
                    }
                    return t;
                }
                return __assign.apply(this, arguments);
            }

            function __rest(s, e) {
                var t = {};
                for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
                    t[p] = s[p];
                if (s != null && typeof Object.getOwnPropertySymbols === "function")
                    for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
                        if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                            t[p[i]] = s[p[i]];
                    }
                return t;
            }

            function __decorate(decorators, target, key, desc) {
                var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
                if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
                else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
                return c > 3 && r && Object.defineProperty(target, key, r), r;
            }

            function __param(paramIndex, decorator) {
                return function (target, key) { decorator(target, key, paramIndex); }
            }

            function __metadata(metadataKey, metadataValue) {
                if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
            }

            function __awaiter(thisArg, _arguments, P, generator) {
                function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
                return new (P || (P = Promise))(function (resolve, reject) {
                    function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
                    function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
                    function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
                    step((generator = generator.apply(thisArg, _arguments || [])).next());
                });
            }

            function __generator(thisArg, body) {
                var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
                return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
                function verb(n) { return function (v) { return step([n, v]); }; }
                function step(op) {
                    if (f) throw new TypeError("Generator is already executing.");
                    while (_) try {
                        if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
                        if (y = 0, t) op = [op[0] & 2, t.value];
                        switch (op[0]) {
                            case 0: case 1: t = op; break;
                            case 4: _.label++; return { value: op[1], done: false };
                            case 5: _.label++; y = op[1]; op = [0]; continue;
                            case 7: op = _.ops.pop(); _.trys.pop(); continue;
                            default:
                                if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                                if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                                if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                                if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                                if (t[2]) _.ops.pop();
                                _.trys.pop(); continue;
                        }
                        op = body.call(thisArg, _);
                    } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
                    if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
                }
            }

            var __createBinding = Object.create ? (function(o, m, k, k2) {
                if (k2 === undefined) k2 = k;
                var desc = Object.getOwnPropertyDescriptor(m, k);
                if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
                    desc = { enumerable: true, get: function() { return m[k]; } };
                }
                Object.defineProperty(o, k2, desc);
            }) : (function(o, m, k, k2) {
                if (k2 === undefined) k2 = k;
                o[k2] = m[k];
            });

            function __exportStar(m, o) {
                for (var p in m) if (p !== "default" && !Object.prototype.hasOwnProperty.call(o, p)) __createBinding(o, m, p);
            }

            function __values(o) {
                var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
                if (m) return m.call(o);
                if (o && typeof o.length === "number") return {
                    next: function () {
                        if (o && i >= o.length) o = void 0;
                        return { value: o && o[i++], done: !o };
                    }
                };
                throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
            }

            function __read(o, n) {
                var m = typeof Symbol === "function" && o[Symbol.iterator];
                if (!m) return o;
                var i = m.call(o), r, ar = [], e;
                try {
                    while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
                }
                catch (error) { e = { error: error }; }
                finally {
                    try {
                        if (r && !r.done && (m = i["return"])) m.call(i);
                    }
                    finally { if (e) throw e.error; }
                }
                return ar;
            }

            /** @deprecated */
            function __spread() {
                for (var ar = [], i = 0; i < arguments.length; i++)
                    ar = ar.concat(__read(arguments[i]));
                return ar;
            }

            /** @deprecated */
            function __spreadArrays() {
                for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
                for (var r = Array(s), k = 0, i = 0; i < il; i++)
                    for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
                        r[k] = a[j];
                return r;
            }

            function __spreadArray(to, from, pack) {
                if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
                    if (ar || !(i in from)) {
                        if (!ar) ar = Array.prototype.slice.call(from, 0, i);
                        ar[i] = from[i];
                    }
                }
                return to.concat(ar || Array.prototype.slice.call(from));
            }

            function __await(v) {
                return this instanceof __await ? (this.v = v, this) : new __await(v);
            }

            function __asyncGenerator(thisArg, _arguments, generator) {
                if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
                var g = generator.apply(thisArg, _arguments || []), i, q = [];
                return i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i;
                function verb(n) { if (g[n]) i[n] = function (v) { return new Promise(function (a, b) { q.push([n, v, a, b]) > 1 || resume(n, v); }); }; }
                function resume(n, v) { try { step(g[n](v)); } catch (e) { settle(q[0][3], e); } }
                function step(r) { r.value instanceof __await ? Promise.resolve(r.value.v).then(fulfill, reject) : settle(q[0][2], r); }
                function fulfill(value) { resume("next", value); }
                function reject(value) { resume("throw", value); }
                function settle(f, v) { if (f(v), q.shift(), q.length) resume(q[0][0], q[0][1]); }
            }

            function __asyncDelegator(o) {
                var i, p;
                return i = {}, verb("next"), verb("throw", function (e) { throw e; }), verb("return"), i[Symbol.iterator] = function () { return this; }, i;
                function verb(n, f) { i[n] = o[n] ? function (v) { return (p = !p) ? { value: __await(o[n](v)), done: n === "return" } : f ? f(v) : v; } : f; }
            }

            function __asyncValues(o) {
                if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
                var m = o[Symbol.asyncIterator], i;
                return m ? m.call(o) : (o = typeof __values === "function" ? __values(o) : o[Symbol.iterator](), i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i);
                function verb(n) { i[n] = o[n] && function (v) { return new Promise(function (resolve, reject) { v = o[n](v), settle(resolve, reject, v.done, v.value); }); }; }
                function settle(resolve, reject, d, v) { Promise.resolve(v).then(function(v) { resolve({ value: v, done: d }); }, reject); }
            }

            function __makeTemplateObject(cooked, raw) {
                if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
                return cooked;
            };

            var __setModuleDefault = Object.create ? (function(o, v) {
                Object.defineProperty(o, "default", { enumerable: true, value: v });
            }) : function(o, v) {
                o["default"] = v;
            };

            function __importStar(mod) {
                if (mod && mod.__esModule) return mod;
                var result = {};
                if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
                __setModuleDefault(result, mod);
                return result;
            }

            function __importDefault(mod) {
                return (mod && mod.__esModule) ? mod : { default: mod };
            }

            function __classPrivateFieldGet(receiver, state, kind, f) {
                if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
                if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
                return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
            }

            function __classPrivateFieldSet(receiver, state, value, kind, f) {
                if (kind === "m") throw new TypeError("Private method is not writable");
                if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
                if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
                return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
            }

            function __classPrivateFieldIn(state, receiver) {
                if (receiver === null || (typeof receiver !== "object" && typeof receiver !== "function")) throw new TypeError("Cannot use 'in' operator on non-object");
                return typeof state === "function" ? receiver === state : state.has(receiver);
            }


            /***/ }),

        /***/ "./src/mock/provider/PromotionProviderMock.ts":
        /*!****************************************************!*\
  !*** ./src/mock/provider/PromotionProviderMock.ts ***!
  \****************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
                function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
                return new (P || (P = Promise))(function (resolve, reject) {
                    function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
                    function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
                    function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
                    step((generator = generator.apply(thisArg, _arguments || [])).next());
                });
            };
            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            Object.defineProperty(exports, "__esModule", { value: true });
            const DomainItem_1 = __importDefault(__webpack_require__(/*! @/plugin/cms/models/DomainItem */ "./src/plugin/cms/models/DomainItem.ts"));
            const GameProvider_1 = __importDefault(__webpack_require__(/*! @/plugin/cms/models/GameProvider */ "./src/plugin/cms/models/GameProvider.ts"));
            const Promotion_1 = __webpack_require__(/*! @/plugin/promotions/Promotion */ "./src/plugin/promotions/Promotion.ts");
            class FakePromApi {
                constructor() {
                    this._fakeDb = new Map();
                    this.createTestData();
                }
                get() {
                    return new Promise((res) => {
                        setTimeout(() => {
                            res(Array.from(this._fakeDb.values()));
                        }, 500);
                    });
                }
                getById(key) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            const item = this._fakeDb.get(key);
                            res(item || null);
                        }, 500);
                    });
                }
                getBetween(startDate, endDate) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return new Promise((res) => {
                            const wantedProms = [];
                            for (const [key, promotion] of this._fakeDb) {
                                const rule = promotion.schedule.setDetailsByRrule();
                                if (!promotion.schedule.dateStart || !rule) {
                                    continue;
                                }
                                const eventsBetweenDates = rule.between(startDate, endDate, true);
                                const hasEventsBetween = eventsBetweenDates.length > 0;
                                // TODO: This can be a bit more complicated by checking the count and seeing if it's end is
                                // before this month
                                // TODO: Consider single days
                                if (hasEventsBetween) {
                                    wantedProms.push(promotion);
                                }
                            }
                            setTimeout(() => {
                                res(wantedProms);
                            }, 500);
                        });
                    });
                }
                add(i) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            this._fakeDb.set(i.id, i);
                            setTimeout(() => {
                                res();
                            }, 250);
                        }, 250);
                    });
                }
                update(id, i) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            if (this._fakeDb.has(id)) {
                                this._fakeDb.set(id, i);
                            }
                            setTimeout(() => {
                                res();
                            }, 250);
                        }, 250);
                    });
                }
                createTestData() {
                    const domain = new DomainItem_1.default('livescore_uk', 'livescore_uk', true);
                    const provider = new GameProvider_1.default('roxor', [], 'livescore_uk', 'svc-reward-pr-casino-roxor');
                    const single = new Promotion_1.Promotion();
                    single.title = 'Single Event Promotion';
                    single.schedule.rruleString = 'RRULE:FREQ=YEARLY;INTERVAL=1;COUNT=1';
                    const recur = new Promotion_1.Promotion();
                    recur.title = 'Recurring Event Promotion';
                    recur.schedule.rruleString = 'RRULE:FREQ=WEEKLY;INTERVAL=2;COUNT=3';
                    single.domain = domain;
                    recur.domain = domain;
                    this._fakeDb.set(single.id, single);
                    this._fakeDb.set(recur.id, recur);
                }
            }
            class PromotionProviderMock {
                constructor() {
                    this._api = new FakePromApi();
                }
                get() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this._api.get();
                    });
                }
                getById(id) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this._api.getById(id);
                    });
                }
                getPromotionsBetween(start, end) {
                    return __awaiter(this, void 0, void 0, function* () {
                        const startDate = new Date(start.year, start.month - 1, start.day);
                        const endDate = new Date(end.year, end.month - 1, end.day);
                        return yield this._api.getBetween(startDate, endDate);
                    });
                }
                add(promotion) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this._api.add(promotion);
                    });
                }
                update(id, promotion) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this._api.update(id, promotion);
                    });
                }
            }
            exports.default = PromotionProviderMock;
            ;
            window.VuePromotionsProvider = new PromotionProviderMock();


            /***/ }),

        /***/ "./src/plugin/cms/models/DomainItem.ts":
        /*!*********************************************!*\
  !*** ./src/plugin/cms/models/DomainItem.ts ***!
  \*********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class DomainItem {
                constructor(displayName, name, pd) {
                    this.displayName = displayName;
                    this.name = name;
                    this.pd = pd;
                    this.randomId = nanoid_1.nanoid();
                }
            }
            exports.default = DomainItem;


            /***/ }),

        /***/ "./src/plugin/cms/models/GameProvider.ts":
        /*!***********************************************!*\
  !*** ./src/plugin/cms/models/GameProvider.ts ***!
  \***********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            exports.ExtraField = exports.Activity = void 0;
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class Activity {
                constructor(name, promoProvider) {
                    this.name = name;
                    this.promoProvider = promoProvider;
                    this.id = nanoid_1.nanoid();
                }
            }
            exports.Activity = Activity;
            class ExtraField {
                constructor(name, promoProvider = -1, dataType = null, description = null) {
                    this.name = name;
                    this.promoProvider = promoProvider;
                    this.dataType = dataType;
                    this.description = description;
                    this.id = nanoid_1.nanoid();
                }
            }
            exports.ExtraField = ExtraField;
            class GameProvider {
                constructor(name, games = [], domain, url,
                            // + For promotions
                            category = null, activities = [], extraFields = [] // -
                ) {
                    this.name = name;
                    this.games = games;
                    this.domain = domain;
                    this.url = url;
                    this.category = category;
                    this.activities = activities;
                    this.extraFields = extraFields;
                    this.id = nanoid_1.nanoid();
                    this.active = false;
                }
                addGame(game) {
                    this.games.push(game);
                }
            }
            exports.default = GameProvider;


            /***/ }),

        /***/ "./src/plugin/promotions/Promotion.ts":
        /*!********************************************!*\
  !*** ./src/plugin/promotions/Promotion.ts ***!
  \********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            exports.Promotion = exports.PromotionTheme = void 0;
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            const rrule_1 = __webpack_require__(/*! rrule */ "./node_modules/rrule/dist/esm/index.js");
            class Schedule {
                constructor() {
                    this.id = nanoid_1.nanoid();
                    this.rruleString = '';
                    this.lengthInDays = 1;
                    this.dateStart = null;
                    this.dateUntil = null;
                    this.singleDay = true;
                }
                setDetailsByRrule() {
                    if (!this.rruleString) {
                        return;
                    }
                    const rrule = rrule_1.RRule.fromString(this.rruleString);
                    this.dateStart = rrule.options.dtstart;
                    this.dateUntil = rrule.options.until;
                    this.singleDay = rrule.options.count === null || rrule.options.count <= 1;
                    return rrule;
                }
            }
            class PromotionTheme {
                constructor() {
                    this.colorName = 'primary';
                    this.colorHex = '#1976d2';
                }
                get color() {
                    return this.colorHex || this.color;
                }
            }
            exports.PromotionTheme = PromotionTheme;
            class Promotion {
                constructor() {
                    this.id = nanoid_1.nanoid();
                    this.title = '';
                    this.description = '';
                    this.schedule = new Schedule();
                    this.reward = null;
                    // provider: GameProvider | null = null
                    this.category = null;
                    this.challengeGroups = []; // A list of groups of challenges, either needs to be completed
                    this.domain = null;
                    this.theme = new PromotionTheme();
                }
            }
            exports.Promotion = Promotion;


            /***/ })

        /******/ });
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL25hbm9pZC9pbmRleC5icm93c2VyLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9uYW5vaWQvdXJsLWFscGhhYmV0L2luZGV4LmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9jYWNoZS5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vY2FsbGJhY2tpdGVycmVzdWx0LmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9kYXRldGltZS5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vZGF0ZXV0aWwuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL2RhdGV3aXRoem9uZS5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaGVscGVycy5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaW5kZXguanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL2l0ZXIvaW5kZXguanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL2l0ZXIvcG9zbGlzdC5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaXRlcmluZm8vZWFzdGVyLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9pdGVyaW5mby9pbmRleC5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaXRlcmluZm8vbW9udGhpbmZvLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9pdGVyaW5mby95ZWFyaW5mby5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaXRlcnJlc3VsdC5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vaXRlcnNldC5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vbWFza3MuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL25scC9pMThuLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9ubHAvaW5kZXguanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL25scC9wYXJzZXRleHQuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL25scC90b3RleHQuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL29wdGlvbnN0b3N0cmluZy5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vcGFyc2VvcHRpb25zLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9wYXJzZXN0cmluZy5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vcnJ1bGUuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL3JydWxlc2V0LmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9ycnVsZS9kaXN0L2VzbS9ycnVsZXN0ci5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcnJ1bGUvZGlzdC9lc20vdHlwZXMuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL2Rpc3QvZXNtL3dlZWtkYXkuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL3JydWxlL25vZGVfbW9kdWxlcy90c2xpYi90c2xpYi5lczYuanMiLCJ3ZWJwYWNrOi8vLy4vc3JjL21vY2svcHJvdmlkZXIvUHJvbW90aW9uUHJvdmlkZXJNb2NrLnRzIiwid2VicGFjazovLy8uL3NyYy9wbHVnaW4vY21zL21vZGVscy9Eb21haW5JdGVtLnRzIiwid2VicGFjazovLy8uL3NyYy9wbHVnaW4vY21zL21vZGVscy9HYW1lUHJvdmlkZXIudHMiLCJ3ZWJwYWNrOi8vLy4vc3JjL3BsdWdpbi9wcm9tb3Rpb25zL1Byb21vdGlvbi50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO1FBQUE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7O0FDbEZBO0FBQUE7QUFBQTtBQUFBO0FBQUE7QUFBQTtBQUFBO0FBQUE7QUFBcUQ7QUFDOUM7QUFDQTtBQUNQO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDTztBQUNQO0FBQ087QUFD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QUs7QUFDcEM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsK0JBQStCLDRDQUFLO0FBQ3BDO0FBQ0E7QUFDQTtBQUNBLHFDQUFxQyw0Q0FBSztBQUMxQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLDJDQUEyQyw0Q0FBSztBQUNoRDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsK0JBQStCLDRDQUFLO0FBQ3BDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsMkNBQTJDLDRDQUFLO0FBQ2hEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsdUNBQXVDLDRDQUFLO0FBQzVDO0FBQ0E7QUFDQTtBQUNBO0FBQ0EseUNBQXlDLDRDQUFLLEtBQUssNENBQUssS0FBSyw0Q0FBSyxLQUFLLDRDQUFLLEtBQUssNENBQUs7QUFDdEY7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsOEVBQThFO0FBQzlFO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsU0FBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwwRUFBMEU7QUFDMUU7QUFD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O0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHNCQUFzQix5REFBUTtBQUM5QjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLCtCQUErQixhQUFhO0FBQzVDLGFBQWEsd0RBQU87QUFDcEI7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkIsa0JBQWtCO0FBQzdDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7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QzVEO0FBQ0EsaUJBQWlCLG1CQUFPLENBQUMsc0RBQVE7QUFDakMsZ0JBQWdCLG1CQUFPLENBQUMscURBQU87QUFDL0I7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esa0NBQWtDO0FBQ2xDO0FBQ0E7QUFDQTtBQUNBO0FBQ0EiLCJmaWxlIjoiUHJvbW90aW9uUHJvdmlkZXJNb2NrLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pIHtcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcbiBcdFx0fVxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0aTogbW9kdWxlSWQsXG4gXHRcdFx0bDogZmFsc2UsXG4gXHRcdFx0ZXhwb3J0czoge31cbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gZGVmaW5lIGdldHRlciBmdW5jdGlvbiBmb3IgaGFybW9ueSBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmQgPSBmdW5jdGlvbihleHBvcnRzLCBuYW1lLCBnZXR0ZXIpIHtcbiBcdFx0aWYoIV9fd2VicGFja19yZXF1aXJlX18ubyhleHBvcnRzLCBuYW1lKSkge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBuYW1lLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZ2V0dGVyIH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSBmdW5jdGlvbihleHBvcnRzKSB7XG4gXHRcdGlmKHR5cGVvZiBTeW1ib2wgIT09ICd1bmRlZmluZWQnICYmIFN5bWJvbC50b1N0cmluZ1RhZykge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBTeW1ib2wudG9TdHJpbmdUYWcsIHsgdmFsdWU6ICdNb2R1bGUnIH0pO1xuIFx0XHR9XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCAnX19lc01vZHVsZScsIHsgdmFsdWU6IHRydWUgfSk7XG4gXHR9O1xuXG4gXHQvLyBjcmVhdGUgYSBmYWtlIG5hbWVzcGFjZSBvYmplY3RcbiBcdC8vIG1vZGUgJiAxOiB2YWx1ZSBpcyBhIG1vZHVsZSBpZCwgcmVxdWlyZSBpdFxuIFx0Ly8gbW9kZSAmIDI6IG1lcmdlIGFsbCBwcm9wZXJ0aWVzIG9mIHZhbHVlIGludG8gdGhlIG5zXG4gXHQvLyBtb2RlICYgNDogcmV0dXJuIHZhbHVlIHdoZW4gYWxyZWFkeSBucyBvYmplY3RcbiBcdC8vIG1vZGUgJiA4fDE6IGJlaGF2ZSBsaWtlIHJlcXVpcmVcbiBcdF9fd2VicGFja19yZXF1aXJlX18udCA9IGZ1bmN0aW9uKHZhbHVlLCBtb2RlKSB7XG4gXHRcdGlmKG1vZGUgJiAxKSB2YWx1ZSA9IF9fd2VicGFja19yZXF1aXJlX18odmFsdWUpO1xuIFx0XHRpZihtb2RlICYgOCkgcmV0dXJuIHZhbHVlO1xuIFx0XHRpZigobW9kZSAmIDQpICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCcgJiYgdmFsdWUgJiYgdmFsdWUuX19lc01vZHVsZSkgcmV0dXJuIHZhbHVlO1xuIFx0XHR2YXIgbnMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuIFx0XHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIobnMpO1xuIFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkobnMsICdkZWZhdWx0JywgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdmFsdWUgfSk7XG4gXHRcdGlmKG1vZGUgJiAyICYmIHR5cGVvZiB2YWx1ZSAhPSAnc3RyaW5nJykgZm9yKHZhciBrZXkgaW4gdmFsdWUpIF9fd2VicGFja19yZXF1aXJlX18uZChucywga2V5LCBmdW5jdGlvbihrZXkpIHsgcmV0dXJuIHZhbHVlW2tleV07IH0uYmluZChudWxsLCBrZXkpKTtcbiBcdFx0cmV0dXJuIG5zO1xuIFx0fTtcblxuIFx0Ly8gZ2V0RGVmYXVsdEV4cG9ydCBmdW5jdGlvbiBmb3IgY29tcGF0aWJpbGl0eSB3aXRoIG5vbi1oYXJtb255IG1vZHVsZXNcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubiA9IGZ1bmN0aW9uKG1vZHVsZSkge1xuIFx0XHR2YXIgZ2V0dGVyID0gbW9kdWxlICYmIG1vZHVsZS5fX2VzTW9kdWxlID9cbiBcdFx0XHRmdW5jdGlvbiBnZXREZWZhdWx0KCkgeyByZXR1cm4gbW9kdWxlWydkZWZhdWx0J107IH0gOlxuIFx0XHRcdGZ1bmN0aW9uIGdldE1vZHVsZUV4cG9ydHMoKSB7IHJldHVybiBtb2R1bGU7IH07XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18uZChnZXR0ZXIsICdhJywgZ2V0dGVyKTtcbiBcdFx0cmV0dXJuIGdldHRlcjtcbiBcdH07XG5cbiBcdC8vIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbFxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5vID0gZnVuY3Rpb24ob2JqZWN0LCBwcm9wZXJ0eSkgeyByZXR1cm4gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG9iamVjdCwgcHJvcGVydHkpOyB9O1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IFwiLi9zcmMvbW9jay9wcm92aWRlci9Qcm9tb3Rpb25Qcm92aWRlck1vY2sudHNcIik7XG4iLCJleHBvcnQgeyB1cmxBbHBoYWJldCB9IGZyb20gJy4vdXJsLWFscGhhYmV0L2luZGV4LmpzJ1xuZXhwb3J0IGxldCByYW5kb20gPSBieXRlcyA9PiBjcnlwdG8uZ2V0UmFuZG9tVmFsdWVzKG5ldyBVaW50OEFycmF5KGJ5dGVzKSlcbmV4cG9ydCBsZXQgY3VzdG9tUmFuZG9tID0gKGFscGhhYmV0LCBkZWZhdWx0U2l6ZSwgZ2V0UmFuZG9tKSA9PiB7XG4gIGxldCBtYXNrID0gKDIgPDwgKE1hdGgubG9nKGFscGhhYmV0Lmxlbmd0aCAtIDEpIC8gTWF0aC5MTjIpKSAtIDFcbiAgbGV0IHN0ZXAgPSAtfigoMS42ICogbWFzayAqIGRlZmF1bHRTaXplKSAvIGFscGhhYmV0Lmxlbmd0aClcbiAgcmV0dXJuIChzaXplID0gZGVmYXVsdFNpemUpID0+IHtcbiAgICBsZXQgaWQgPSAnJ1xuICAgIHdoaWxlICh0cnVlKSB7XG4gICAgICBsZXQgYnl0ZXMgPSBnZXRSYW5kb20oc3RlcClcbiAgICAgIGxldCBqID0gc3RlcFxuICAgICAgd2hpbGUgKGotLSkge1xuICAgICAgICBpZCArPSBhbHBoYWJldFtieXRlc1tqXSAmIG1hc2tdIHx8ICcnXG4gICAgICAgIGlmIChpZC5sZW5ndGggPT09IHNpemUpIHJldHVybiBpZFxuICAgICAgfVxuICAgIH1cbiAgfVxufVxuZXhwb3J0IGxldCBjdXN0b21BbHBoYWJldCA9IChhbHBoYWJldCwgc2l6ZSA9IDIxKSA9PlxuICBjdXN0b21SYW5kb20oYWxwaGFiZXQsIHNpemUsIHJhbmRvbSlcbmV4cG9ydCBsZXQgbmFub2lkID0gKHNpemUgPSAyMSkgPT5cbiAgY3J5cHRvLmdldFJhbmRvbVZhbHVlcyhuZXcgVWludDhBcnJheShzaXplKSkucmVkdWNlKChpZCwgYnl0ZSkgPT4ge1xuICAgIGJ5dGUgJj0gNjNcbiAgICBpZiAoYnl0ZSA8IDM2KSB7XG4gICAgICBpZCArPSBieXRlLnRvU3RyaW5nKDM2KVxuICAgIH0gZWxzZSBpZiAoYnl0ZSA8IDYyKSB7XG4gICAgICBpZCArPSAoYnl0ZSAtIDI2KS50b1N0cmluZygzNikudG9VcHBlckNhc2UoKVxuICAgIH0gZWxzZSBpZiAoYnl0ZSA+IDYyKSB7XG4gICAgICBpZCArPSAnLSdcbiAgICB9IGVsc2Uge1xuICAgICAgaWQgKz0gJ18nXG4gICAgfVxuICAgIHJldHVybiBpZFxuICB9LCAnJylcbiIsImV4cG9ydCBjb25zdCB1cmxBbHBoYWJldCA9XG4gICd1c2VhbmRvbS0yNlQxOTgzNDBQWDc1cHhKQUNLVkVSWU1JTkRCVVNIV09MRl9HUVpiZmdoamtscXZ3eXpyaWN0J1xuIiwiaW1wb3J0IEl0ZXJSZXN1bHQgZnJvbSAnLi9pdGVycmVzdWx0JztcbmltcG9ydCBkYXRldXRpbCBmcm9tICcuL2RhdGV1dGlsJztcbmltcG9ydCB7IGlzQXJyYXkgfSBmcm9tICcuL2hlbHBlcnMnO1xuZnVuY3Rpb24gYXJnc01hdGNoKGxlZnQsIHJpZ2h0KSB7XG4gICAgaWYgKEFycmF5LmlzQXJyYXkobGVmdCkpIHtcbiAgICAgICAgaWYgKCFBcnJheS5pc0FycmF5KHJpZ2h0KSlcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgaWYgKGxlZnQubGVuZ3RoICE9PSByaWdodC5sZW5ndGgpXG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIHJldHVybiBsZWZ0LmV2ZXJ5KGZ1bmN0aW9uIChkYXRlLCBpKSB7IHJldHVybiBkYXRlLmdldFRpbWUoKSA9PT0gcmlnaHRbaV0uZ2V0VGltZSgpOyB9KTtcbiAgICB9XG4gICAgaWYgKGxlZnQgaW5zdGFuY2VvZiBEYXRlKSB7XG4gICAgICAgIHJldHVybiByaWdodCBpbnN0YW5jZW9mIERhdGUgJiYgbGVmdC5nZXRUaW1lKCkgPT09IHJpZ2h0LmdldFRpbWUoKTtcbiAgICB9XG4gICAgcmV0dXJuIGxlZnQgPT09IHJpZ2h0O1xufVxudmFyIENhY2hlID0gLyoqIEBjbGFzcyAqLyAoZnVuY3Rpb24gKCkge1xuICAgIGZ1bmN0aW9uIENhY2hlKCkge1xuICAgICAgICB0aGlzLmFsbCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmJlZm9yZSA9IFtdO1xuICAgICAgICB0aGlzLmFmdGVyID0gW107XG4gICAgICAgIHRoaXMuYmV0d2VlbiA9IFtdO1xuICAgIH1cbiAgICAvKipcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gd2hhdCAtIGFsbC9iZWZvcmUvYWZ0ZXIvYmV0d2VlblxuICAgICAqIEBwYXJhbSB7QXJyYXksRGF0ZX0gdmFsdWUgLSBhbiBhcnJheSBvZiBkYXRlcywgb25lIGRhdGUsIG9yIG51bGxcbiAgICAgKiBAcGFyYW0ge09iamVjdD99IGFyZ3MgLSBfaXRlciBhcmd1bWVudHNcbiAgICAgKi9cbiAgICBDYWNoZS5wcm90b3R5cGUuX2NhY2hlQWRkID0gZnVuY3Rpb24gKHdoYXQsIHZhbHVlLCBhcmdzKSB7XG4gICAgICAgIGlmICh2YWx1ZSkge1xuICAgICAgICAgICAgdmFsdWUgPVxuICAgICAgICAgICAgICAgIHZhbHVlIGluc3RhbmNlb2YgRGF0ZVxuICAgICAgICAgICAgICAgICAgICA/IGRhdGV1dGlsLmNsb25lKHZhbHVlKVxuICAgICAgICAgICAgICAgICAgICA6IGRhdGV1dGlsLmNsb25lRGF0ZXModmFsdWUpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh3aGF0ID09PSAnYWxsJykge1xuICAgICAgICAgICAgdGhpcy5hbGwgPSB2YWx1ZTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGFyZ3MuX3ZhbHVlID0gdmFsdWU7XG4gICAgICAgICAgICB0aGlzW3doYXRdLnB1c2goYXJncyk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEByZXR1cm4gZmFsc2UgLSBub3QgaW4gdGhlIGNhY2hlXG4gICAgICogQHJldHVybiBudWxsICAtIGNhY2hlZCwgYnV0IHplcm8gb2NjdXJyZW5jZXMgKGJlZm9yZS9hZnRlcilcbiAgICAgKiBAcmV0dXJuIERhdGUgIC0gY2FjaGVkIChiZWZvcmUvYWZ0ZXIpXG4gICAgICogQHJldHVybiBbXSAgICAtIGNhY2hlZCwgYnV0IHplcm8gb2NjdXJyZW5jZXMgKGFsbC9iZXR3ZWVuKVxuICAgICAqIEByZXR1cm4gW0RhdGUxLCBEYXRlTl0gLSBjYWNoZWQgKGFsbC9iZXR3ZWVuKVxuICAgICAqL1xuICAgIENhY2hlLnByb3RvdHlwZS5fY2FjaGVHZXQgPSBmdW5jdGlvbiAod2hhdCwgYXJncykge1xuICAgICAgICB2YXIgY2FjaGVkID0gZmFsc2U7XG4gICAgICAgIHZhciBhcmdzS2V5cyA9IGFyZ3MgPyBPYmplY3Qua2V5cyhhcmdzKSA6IFtdO1xuICAgICAgICB2YXIgZmluZENhY2hlRGlmZiA9IGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGFyZ3NLZXlzLmxlbmd0aDsgaSsrKSB7XG4gICAgICAgICAgICAgICAgdmFyIGtleSA9IGFyZ3NLZXlzW2ldO1xuICAgICAgICAgICAgICAgIGlmICghYXJnc01hdGNoKGFyZ3Nba2V5XSwgaXRlbVtrZXldKSkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH07XG4gICAgICAgIHZhciBjYWNoZWRPYmplY3QgPSB0aGlzW3doYXRdO1xuICAgICAgICBpZiAod2hhdCA9PT0gJ2FsbCcpIHtcbiAgICAgICAgICAgIGNhY2hlZCA9IHRoaXMuYWxsO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKGlzQXJyYXkoY2FjaGVkT2JqZWN0KSkge1xuICAgICAgICAgICAgLy8gTGV0J3Mgc2VlIHdoZXRoZXIgd2UndmUgYWxyZWFkeSBjYWxsZWQgdGhlXG4gICAgICAgICAgICAvLyAnd2hhdCcgbWV0aG9kIHdpdGggdGhlIHNhbWUgJ2FyZ3MnXG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGNhY2hlZE9iamVjdC5sZW5ndGg7IGkrKykge1xuICAgICAgICAgICAgICAgIHZhciBpdGVtID0gY2FjaGVkT2JqZWN0W2ldO1xuICAgICAgICAgICAgICAgIGlmIChhcmdzS2V5cy5sZW5ndGggJiYgZmluZENhY2hlRGlmZihpdGVtKSlcbiAgICAgICAgICAgICAgICAgICAgY29udGludWU7XG4gICAgICAgICAgICAgICAgY2FjaGVkID0gaXRlbS5fdmFsdWU7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgaWYgKCFjYWNoZWQgJiYgdGhpcy5hbGwpIHtcbiAgICAgICAgICAgIC8vIE5vdCBpbiB0aGUgY2FjaGUsIGJ1dCB3ZSBhbHJlYWR5IGtub3cgYWxsIHRoZSBvY2N1cnJlbmNlcyxcbiAgICAgICAgICAgIC8vIHNvIHdlIGNhbiBmaW5kIHRoZSBjb3JyZWN0IGRhdGVzIGZyb20gdGhlIGNhY2hlZCBvbmVzLlxuICAgICAgICAgICAgdmFyIGl0ZXJSZXN1bHQgPSBuZXcgSXRlclJlc3VsdCh3aGF0LCBhcmdzKTtcbiAgICAgICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgdGhpcy5hbGwubGVuZ3RoOyBpKyspIHtcbiAgICAgICAgICAgICAgICBpZiAoIWl0ZXJSZXN1bHQuYWNjZXB0KHRoaXMuYWxsW2ldKSlcbiAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBjYWNoZWQgPSBpdGVyUmVzdWx0LmdldFZhbHVlKCk7XG4gICAgICAgICAgICB0aGlzLl9jYWNoZUFkZCh3aGF0LCBjYWNoZWQsIGFyZ3MpO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiBpc0FycmF5KGNhY2hlZClcbiAgICAgICAgICAgID8gZGF0ZXV0aWwuY2xvbmVEYXRlcyhjYWNoZWQpXG4gICAgICAgICAgICA6IGNhY2hlZCBpbnN0YW5jZW9mIERhdGVcbiAgICAgICAgICAgICAgICA/IGRhdGV1dGlsLmNsb25lKGNhY2hlZClcbiAgICAgICAgICAgICAgICA6IGNhY2hlZDtcbiAgICB9O1xuICAgIHJldHVybiBDYWNoZTtcbn0oKSk7XG5leHBvcnQgeyBDYWNoZSB9O1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9Y2FjaGUuanMubWFwIiwiaW1wb3J0IHsgX19leHRlbmRzIH0gZnJvbSBcInRzbGliXCI7XG5pbXBvcnQgSXRlclJlc3VsdCBmcm9tICcuL2l0ZXJyZXN1bHQnO1xuLyoqXG4gKiBJdGVyUmVzdWx0IHN1YmNsYXNzIHRoYXQgY2FsbHMgYSBjYWxsYmFjayBmdW5jdGlvbiBvbiBlYWNoIGFkZCxcbiAqIGFuZCBzdG9wcyBpdGVyYXRpbmcgd2hlbiB0aGUgY2FsbGJhY2sgcmV0dXJucyBmYWxzZS5cbiAqL1xudmFyIENhbGxiYWNrSXRlclJlc3VsdCA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uIChfc3VwZXIpIHtcbiAgICBfX2V4dGVuZHMoQ2FsbGJhY2tJdGVyUmVzdWx0LCBfc3VwZXIpO1xuICAgIGZ1bmN0aW9uIENhbGxiYWNrSXRlclJlc3VsdChtZXRob2QsIGFyZ3MsIGl0ZXJhdG9yKSB7XG4gICAgICAgIHZhciBfdGhpcyA9IF9zdXBlci5jYWxsKHRoaXMsIG1ldGhvZCwgYXJncykgfHwgdGhpcztcbiAgICAgICAgX3RoaXMuaXRlcmF0b3IgPSBpdGVyYXRvcjtcbiAgICAgICAgcmV0dXJuIF90aGlzO1xuICAgIH1cbiAgICBDYWxsYmFja0l0ZXJSZXN1bHQucHJvdG90eXBlLmFkZCA9IGZ1bmN0aW9uIChkYXRlKSB7XG4gICAgICAgIGlmICh0aGlzLml0ZXJhdG9yKGRhdGUsIHRoaXMuX3Jlc3VsdC5sZW5ndGgpKSB7XG4gICAgICAgICAgICB0aGlzLl9yZXN1bHQucHVzaChkYXRlKTtcbiAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiBmYWxzZTtcbiAgICB9O1xuICAgIHJldHVybiBDYWxsYmFja0l0ZXJSZXN1bHQ7XG59KEl0ZXJSZXN1bHQpKTtcbmV4cG9ydCBkZWZhdWx0IENhbGxiYWNrSXRlclJlc3VsdDtcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPWNhbGxiYWNraXRlcnJlc3VsdC5qcy5tYXAiLCJpbXBvcnQgeyBfX2V4dGVuZHMgfSBmcm9tIFwidHNsaWJcIjtcbmltcG9ydCB7IEZyZXF1ZW5jeSB9IGZyb20gJy4vdHlwZXMnO1xuaW1wb3J0IHsgcHltb2QsIGRpdm1vZCwgZW1wdHksIGluY2x1ZGVzIH0gZnJvbSAnLi9oZWxwZXJzJztcbmltcG9ydCB7IGRhdGV1dGlsIH0gZnJvbSAnLi9kYXRldXRpbCc7XG52YXIgVGltZSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBUaW1lKGhvdXIsIG1pbnV0ZSwgc2Vjb25kLCBtaWxsaXNlY29uZCkge1xuICAgICAgICB0aGlzLmhvdXIgPSBob3VyO1xuICAgICAgICB0aGlzLm1pbnV0ZSA9IG1pbnV0ZTtcbiAgICAgICAgdGhpcy5zZWNvbmQgPSBzZWNvbmQ7XG4gICAgICAgIHRoaXMubWlsbGlzZWNvbmQgPSBtaWxsaXNlY29uZCB8fCAwO1xuICAgIH1cbiAgICBUaW1lLnByb3RvdHlwZS5nZXRIb3VycyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuaG91cjtcbiAgICB9O1xuICAgIFRpbWUucHJvdG90eXBlLmdldE1pbnV0ZXMgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLm1pbnV0ZTtcbiAgICB9O1xuICAgIFRpbWUucHJvdG90eXBlLmdldFNlY29uZHMgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLnNlY29uZDtcbiAgICB9O1xuICAgIFRpbWUucHJvdG90eXBlLmdldE1pbGxpc2Vjb25kcyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMubWlsbGlzZWNvbmQ7XG4gICAgfTtcbiAgICBUaW1lLnByb3RvdHlwZS5nZXRUaW1lID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gKCh0aGlzLmhvdXIgKiA2MCAqIDYwICsgdGhpcy5taW51dGUgKiA2MCArIHRoaXMuc2Vjb25kKSAqIDEwMDAgK1xuICAgICAgICAgICAgdGhpcy5taWxsaXNlY29uZCk7XG4gICAgfTtcbiAgICByZXR1cm4gVGltZTtcbn0oKSk7XG5leHBvcnQgeyBUaW1lIH07XG52YXIgRGF0ZVRpbWUgPSAvKiogQGNsYXNzICovIChmdW5jdGlvbiAoX3N1cGVyKSB7XG4gICAgX19leHRlbmRzKERhdGVUaW1lLCBfc3VwZXIpO1xuICAgIGZ1bmN0aW9uIERhdGVUaW1lKHllYXIsIG1vbnRoLCBkYXksIGhvdXIsIG1pbnV0ZSwgc2Vjb25kLCBtaWxsaXNlY29uZCkge1xuICAgICAgICB2YXIgX3RoaXMgPSBfc3VwZXIuY2FsbCh0aGlzLCBob3VyLCBtaW51dGUsIHNlY29uZCwgbWlsbGlzZWNvbmQpIHx8IHRoaXM7XG4gICAgICAgIF90aGlzLnllYXIgPSB5ZWFyO1xuICAgICAgICBfdGhpcy5tb250aCA9IG1vbnRoO1xuICAgICAgICBfdGhpcy5kYXkgPSBkYXk7XG4gICAgICAgIHJldHVybiBfdGhpcztcbiAgICB9XG4gICAgRGF0ZVRpbWUuZnJvbURhdGUgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICByZXR1cm4gbmV3IHRoaXMoZGF0ZS5nZXRVVENGdWxsWWVhcigpLCBkYXRlLmdldFVUQ01vbnRoKCkgKyAxLCBkYXRlLmdldFVUQ0RhdGUoKSwgZGF0ZS5nZXRVVENIb3VycygpLCBkYXRlLmdldFVUQ01pbnV0ZXMoKSwgZGF0ZS5nZXRVVENTZWNvbmRzKCksIGRhdGUudmFsdWVPZigpICUgMTAwMCk7XG4gICAgfTtcbiAgICBEYXRlVGltZS5wcm90b3R5cGUuZ2V0V2Vla2RheSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIGRhdGV1dGlsLmdldFdlZWtkYXkobmV3IERhdGUodGhpcy5nZXRUaW1lKCkpKTtcbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5nZXRUaW1lID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gbmV3IERhdGUoRGF0ZS5VVEModGhpcy55ZWFyLCB0aGlzLm1vbnRoIC0gMSwgdGhpcy5kYXksIHRoaXMuaG91ciwgdGhpcy5taW51dGUsIHRoaXMuc2Vjb25kLCB0aGlzLm1pbGxpc2Vjb25kKSkuZ2V0VGltZSgpO1xuICAgIH07XG4gICAgRGF0ZVRpbWUucHJvdG90eXBlLmdldERheSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuZGF5O1xuICAgIH07XG4gICAgRGF0ZVRpbWUucHJvdG90eXBlLmdldE1vbnRoID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5tb250aDtcbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5nZXRZZWFyID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy55ZWFyO1xuICAgIH07XG4gICAgRGF0ZVRpbWUucHJvdG90eXBlLmFkZFllYXJzID0gZnVuY3Rpb24gKHllYXJzKSB7XG4gICAgICAgIHRoaXMueWVhciArPSB5ZWFycztcbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5hZGRNb250aHMgPSBmdW5jdGlvbiAobW9udGhzKSB7XG4gICAgICAgIHRoaXMubW9udGggKz0gbW9udGhzO1xuICAgICAgICBpZiAodGhpcy5tb250aCA+IDEyKSB7XG4gICAgICAgICAgICB2YXIgeWVhckRpdiA9IE1hdGguZmxvb3IodGhpcy5tb250aCAvIDEyKTtcbiAgICAgICAgICAgIHZhciBtb250aE1vZCA9IHB5bW9kKHRoaXMubW9udGgsIDEyKTtcbiAgICAgICAgICAgIHRoaXMubW9udGggPSBtb250aE1vZDtcbiAgICAgICAgICAgIHRoaXMueWVhciArPSB5ZWFyRGl2O1xuICAgICAgICAgICAgaWYgKHRoaXMubW9udGggPT09IDApIHtcbiAgICAgICAgICAgICAgICB0aGlzLm1vbnRoID0gMTI7XG4gICAgICAgICAgICAgICAgLS10aGlzLnllYXI7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5hZGRXZWVrbHkgPSBmdW5jdGlvbiAoZGF5cywgd2tzdCkge1xuICAgICAgICBpZiAod2tzdCA+IHRoaXMuZ2V0V2Vla2RheSgpKSB7XG4gICAgICAgICAgICB0aGlzLmRheSArPSAtKHRoaXMuZ2V0V2Vla2RheSgpICsgMSArICg2IC0gd2tzdCkpICsgZGF5cyAqIDc7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aGlzLmRheSArPSAtKHRoaXMuZ2V0V2Vla2RheSgpIC0gd2tzdCkgKyBkYXlzICogNztcbiAgICAgICAgfVxuICAgICAgICB0aGlzLmZpeERheSgpO1xuICAgIH07XG4gICAgRGF0ZVRpbWUucHJvdG90eXBlLmFkZERhaWx5ID0gZnVuY3Rpb24gKGRheXMpIHtcbiAgICAgICAgdGhpcy5kYXkgKz0gZGF5cztcbiAgICAgICAgdGhpcy5maXhEYXkoKTtcbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5hZGRIb3VycyA9IGZ1bmN0aW9uIChob3VycywgZmlsdGVyZWQsIGJ5aG91cikge1xuICAgICAgICBpZiAoZmlsdGVyZWQpIHtcbiAgICAgICAgICAgIC8vIEp1bXAgdG8gb25lIGl0ZXJhdGlvbiBiZWZvcmUgbmV4dCBkYXlcbiAgICAgICAgICAgIHRoaXMuaG91ciArPSBNYXRoLmZsb29yKCgyMyAtIHRoaXMuaG91cikgLyBob3VycykgKiBob3VycztcbiAgICAgICAgfVxuICAgICAgICBmb3IgKDs7KSB7XG4gICAgICAgICAgICB0aGlzLmhvdXIgKz0gaG91cnM7XG4gICAgICAgICAgICB2YXIgX2EgPSBkaXZtb2QodGhpcy5ob3VyLCAyNCksIGRheURpdiA9IF9hLmRpdiwgaG91ck1vZCA9IF9hLm1vZDtcbiAgICAgICAgICAgIGlmIChkYXlEaXYpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmhvdXIgPSBob3VyTW9kO1xuICAgICAgICAgICAgICAgIHRoaXMuYWRkRGFpbHkoZGF5RGl2KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChlbXB0eShieWhvdXIpIHx8IGluY2x1ZGVzKGJ5aG91ciwgdGhpcy5ob3VyKSlcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgfVxuICAgIH07XG4gICAgRGF0ZVRpbWUucHJvdG90eXBlLmFkZE1pbnV0ZXMgPSBmdW5jdGlvbiAobWludXRlcywgZmlsdGVyZWQsIGJ5aG91ciwgYnltaW51dGUpIHtcbiAgICAgICAgaWYgKGZpbHRlcmVkKSB7XG4gICAgICAgICAgICAvLyBKdW1wIHRvIG9uZSBpdGVyYXRpb24gYmVmb3JlIG5leHQgZGF5XG4gICAgICAgICAgICB0aGlzLm1pbnV0ZSArPVxuICAgICAgICAgICAgICAgIE1hdGguZmxvb3IoKDE0MzkgLSAodGhpcy5ob3VyICogNjAgKyB0aGlzLm1pbnV0ZSkpIC8gbWludXRlcykgKiBtaW51dGVzO1xuICAgICAgICB9XG4gICAgICAgIGZvciAoOzspIHtcbiAgICAgICAgICAgIHRoaXMubWludXRlICs9IG1pbnV0ZXM7XG4gICAgICAgICAgICB2YXIgX2EgPSBkaXZtb2QodGhpcy5taW51dGUsIDYwKSwgaG91ckRpdiA9IF9hLmRpdiwgbWludXRlTW9kID0gX2EubW9kO1xuICAgICAgICAgICAgaWYgKGhvdXJEaXYpIHtcbiAgICAgICAgICAgICAgICB0aGlzLm1pbnV0ZSA9IG1pbnV0ZU1vZDtcbiAgICAgICAgICAgICAgICB0aGlzLmFkZEhvdXJzKGhvdXJEaXYsIGZhbHNlLCBieWhvdXIpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKChlbXB0eShieWhvdXIpIHx8IGluY2x1ZGVzKGJ5aG91ciwgdGhpcy5ob3VyKSkgJiZcbiAgICAgICAgICAgICAgICAoZW1wdHkoYnltaW51dGUpIHx8IGluY2x1ZGVzKGJ5bWludXRlLCB0aGlzLm1pbnV0ZSkpKSB7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIERhdGVUaW1lLnByb3RvdHlwZS5hZGRTZWNvbmRzID0gZnVuY3Rpb24gKHNlY29uZHMsIGZpbHRlcmVkLCBieWhvdXIsIGJ5bWludXRlLCBieXNlY29uZCkge1xuICAgICAgICBpZiAoZmlsdGVyZWQpIHtcbiAgICAgICAgICAgIC8vIEp1bXAgdG8gb25lIGl0ZXJhdGlvbiBiZWZvcmUgbmV4dCBkYXlcbiAgICAgICAgICAgIHRoaXMuc2Vjb25kICs9XG4gICAgICAgICAgICAgICAgTWF0aC5mbG9vcigoODYzOTkgLSAodGhpcy5ob3VyICogMzYwMCArIHRoaXMubWludXRlICogNjAgKyB0aGlzLnNlY29uZCkpIC9cbiAgICAgICAgICAgICAgICAgICAgc2Vjb25kcykgKiBzZWNvbmRzO1xuICAgICAgICB9XG4gICAgICAgIGZvciAoOzspIHtcbiAgICAgICAgICAgIHRoaXMuc2Vjb25kICs9IHNlY29uZHM7XG4gICAgICAgICAgICB2YXIgX2EgPSBkaXZtb2QodGhpcy5zZWNvbmQsIDYwKSwgbWludXRlRGl2ID0gX2EuZGl2LCBzZWNvbmRNb2QgPSBfYS5tb2Q7XG4gICAgICAgICAgICBpZiAobWludXRlRGl2KSB7XG4gICAgICAgICAgICAgICAgdGhpcy5zZWNvbmQgPSBzZWNvbmRNb2Q7XG4gICAgICAgICAgICAgICAgdGhpcy5hZGRNaW51dGVzKG1pbnV0ZURpdiwgZmFsc2UsIGJ5aG91ciwgYnltaW51dGUpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKChlbXB0eShieWhvdXIpIHx8IGluY2x1ZGVzKGJ5aG91ciwgdGhpcy5ob3VyKSkgJiZcbiAgICAgICAgICAgICAgICAoZW1wdHkoYnltaW51dGUpIHx8IGluY2x1ZGVzKGJ5bWludXRlLCB0aGlzLm1pbnV0ZSkpICYmXG4gICAgICAgICAgICAgICAgKGVtcHR5KGJ5c2Vjb25kKSB8fCBpbmNsdWRlcyhieXNlY29uZCwgdGhpcy5zZWNvbmQpKSkge1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfTtcbiAgICBEYXRlVGltZS5wcm90b3R5cGUuZml4RGF5ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICBpZiAodGhpcy5kYXkgPD0gMjgpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB2YXIgZGF5c2lubW9udGggPSBkYXRldXRpbC5tb250aFJhbmdlKHRoaXMueWVhciwgdGhpcy5tb250aCAtIDEpWzFdO1xuICAgICAgICBpZiAodGhpcy5kYXkgPD0gZGF5c2lubW9udGgpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB3aGlsZSAodGhpcy5kYXkgPiBkYXlzaW5tb250aCkge1xuICAgICAgICAgICAgdGhpcy5kYXkgLT0gZGF5c2lubW9udGg7XG4gICAgICAgICAgICArK3RoaXMubW9udGg7XG4gICAgICAgICAgICBpZiAodGhpcy5tb250aCA9PT0gMTMpIHtcbiAgICAgICAgICAgICAgICB0aGlzLm1vbnRoID0gMTtcbiAgICAgICAgICAgICAgICArK3RoaXMueWVhcjtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy55ZWFyID4gZGF0ZXV0aWwuTUFYWUVBUikge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZGF5c2lubW9udGggPSBkYXRldXRpbC5tb250aFJhbmdlKHRoaXMueWVhciwgdGhpcy5tb250aCAtIDEpWzFdO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBEYXRlVGltZS5wcm90b3R5cGUuYWRkID0gZnVuY3Rpb24gKG9wdGlvbnMsIGZpbHRlcmVkKSB7XG4gICAgICAgIHZhciBmcmVxID0gb3B0aW9ucy5mcmVxLCBpbnRlcnZhbCA9IG9wdGlvbnMuaW50ZXJ2YWwsIHdrc3QgPSBvcHRpb25zLndrc3QsIGJ5aG91ciA9IG9wdGlvbnMuYnlob3VyLCBieW1pbnV0ZSA9IG9wdGlvbnMuYnltaW51dGUsIGJ5c2Vjb25kID0gb3B0aW9ucy5ieXNlY29uZDtcbiAgICAgICAgc3dpdGNoIChmcmVxKSB7XG4gICAgICAgICAgICBjYXNlIEZyZXF1ZW5jeS5ZRUFSTFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuYWRkWWVhcnMoaW50ZXJ2YWwpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuTU9OVEhMWTpcbiAgICAgICAgICAgICAgICByZXR1cm4gdGhpcy5hZGRNb250aHMoaW50ZXJ2YWwpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuV0VFS0xZOlxuICAgICAgICAgICAgICAgIHJldHVybiB0aGlzLmFkZFdlZWtseShpbnRlcnZhbCwgd2tzdCk7XG4gICAgICAgICAgICBjYXNlIEZyZXF1ZW5jeS5EQUlMWTpcbiAgICAgICAgICAgICAgICByZXR1cm4gdGhpcy5hZGREYWlseShpbnRlcnZhbCk7XG4gICAgICAgICAgICBjYXNlIEZyZXF1ZW5jeS5IT1VSTFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuYWRkSG91cnMoaW50ZXJ2YWwsIGZpbHRlcmVkLCBieWhvdXIpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuTUlOVVRFTFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuYWRkTWludXRlcyhpbnRlcnZhbCwgZmlsdGVyZWQsIGJ5aG91ciwgYnltaW51dGUpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuU0VDT05ETFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuYWRkU2Vjb25kcyhpbnRlcnZhbCwgZmlsdGVyZWQsIGJ5aG91ciwgYnltaW51dGUsIGJ5c2Vjb25kKTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgcmV0dXJuIERhdGVUaW1lO1xufShUaW1lKSk7XG5leHBvcnQgeyBEYXRlVGltZSB9O1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9ZGF0ZXRpbWUuanMubWFwIiwiLyogZXNsaW50LWRpc2FibGUgQHR5cGVzY3JpcHQtZXNsaW50L25vLW5hbWVzcGFjZSAqL1xuaW1wb3J0IHsgcGFkU3RhcnQgfSBmcm9tICcuL2hlbHBlcnMnO1xuLyoqXG4gKiBHZW5lcmFsIGRhdGUtcmVsYXRlZCB1dGlsaXRpZXMuXG4gKiBBbHNvIGhhbmRsZXMgc2V2ZXJhbCBpbmNvbXBhdGliaWxpdGllcyBiZXR3ZWVuIEphdmFTY3JpcHQgYW5kIFB5dGhvblxuICpcbiAqL1xuZXhwb3J0IHZhciBkYXRldXRpbDtcbihmdW5jdGlvbiAoZGF0ZXV0aWwpIHtcbiAgICBkYXRldXRpbC5NT05USF9EQVlTID0gWzMxLCAyOCwgMzEsIDMwLCAzMSwgMzAsIDMxLCAzMSwgMzAsIDMxLCAzMCwgMzFdO1xuICAgIC8qKlxuICAgICAqIE51bWJlciBvZiBtaWxsaXNlY29uZHMgb2Ygb25lIGRheVxuICAgICAqL1xuICAgIGRhdGV1dGlsLk9ORV9EQVkgPSAxMDAwICogNjAgKiA2MCAqIDI0O1xuICAgIC8qKlxuICAgICAqIEBzZWU6IDxodHRwOi8vZG9jcy5weXRob24ub3JnL2xpYnJhcnkvZGF0ZXRpbWUuaHRtbCNkYXRldGltZS5NQVhZRUFSPlxuICAgICAqL1xuICAgIGRhdGV1dGlsLk1BWFlFQVIgPSA5OTk5O1xuICAgIC8qKlxuICAgICAqIFB5dGhvbiB1c2VzIDEtSmFuLTEgYXMgdGhlIGJhc2UgZm9yIGNhbGN1bGF0aW5nIG9yZGluYWxzIGJ1dCB3ZSBkb24ndFxuICAgICAqIHdhbnQgdG8gY29uZnVzZSB0aGUgSlMgZW5naW5lIHdpdGggbWlsbGlzZWNvbmRzID4gTnVtYmVyLk1BWF9OVU1CRVIsXG4gICAgICogdGhlcmVmb3JlIHdlIHVzZSAxLUphbi0xOTcwIGluc3RlYWRcbiAgICAgKi9cbiAgICBkYXRldXRpbC5PUkRJTkFMX0JBU0UgPSBuZXcgRGF0ZShEYXRlLlVUQygxOTcwLCAwLCAxKSk7XG4gICAgLyoqXG4gICAgICogUHl0aG9uOiBNTy1TVTogMCAtIDZcbiAgICAgKiBKUzogU1UtU0FUIDAgLSA2XG4gICAgICovXG4gICAgZGF0ZXV0aWwuUFlfV0VFS0RBWVMgPSBbNiwgMCwgMSwgMiwgMywgNCwgNV07XG4gICAgLyoqXG4gICAgICogcHlfZGF0ZS50aW1ldHVwbGUoKVs3XVxuICAgICAqL1xuICAgIGRhdGV1dGlsLmdldFllYXJEYXkgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICB2YXIgZGF0ZU5vVGltZSA9IG5ldyBEYXRlKGRhdGUuZ2V0VVRDRnVsbFllYXIoKSwgZGF0ZS5nZXRVVENNb250aCgpLCBkYXRlLmdldFVUQ0RhdGUoKSk7XG4gICAgICAgIHJldHVybiAoTWF0aC5jZWlsKChkYXRlTm9UaW1lLnZhbHVlT2YoKSAtXG4gICAgICAgICAgICBuZXcgRGF0ZShkYXRlLmdldFVUQ0Z1bGxZZWFyKCksIDAsIDEpLnZhbHVlT2YoKSkgL1xuICAgICAgICAgICAgZGF0ZXV0aWwuT05FX0RBWSkgKyAxKTtcbiAgICB9O1xuICAgIGRhdGV1dGlsLmlzTGVhcFllYXIgPSBmdW5jdGlvbiAoeWVhcikge1xuICAgICAgICByZXR1cm4gKHllYXIgJSA0ID09PSAwICYmIHllYXIgJSAxMDAgIT09IDApIHx8IHllYXIgJSA0MDAgPT09IDA7XG4gICAgfTtcbiAgICBkYXRldXRpbC5pc0RhdGUgPSBmdW5jdGlvbiAodmFsdWUpIHtcbiAgICAgICAgcmV0dXJuIHZhbHVlIGluc3RhbmNlb2YgRGF0ZTtcbiAgICB9O1xuICAgIGRhdGV1dGlsLmlzVmFsaWREYXRlID0gZnVuY3Rpb24gKHZhbHVlKSB7XG4gICAgICAgIHJldHVybiBkYXRldXRpbC5pc0RhdGUodmFsdWUpICYmICFpc05hTih2YWx1ZS5nZXRUaW1lKCkpO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogQHJldHVybiB7TnVtYmVyfSB0aGUgZGF0ZSdzIHRpbWV6b25lIG9mZnNldCBpbiBtc1xuICAgICAqL1xuICAgIGRhdGV1dGlsLnR6T2Zmc2V0ID0gZnVuY3Rpb24gKGRhdGUpIHtcbiAgICAgICAgcmV0dXJuIGRhdGUuZ2V0VGltZXpvbmVPZmZzZXQoKSAqIDYwICogMTAwMDtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEBzZWU6IDxodHRwOi8vd3d3Lm1jZmVkcmllcy5jb20vSmF2YVNjcmlwdC9EYXlzQmV0d2Vlbi5hc3A+XG4gICAgICovXG4gICAgZGF0ZXV0aWwuZGF5c0JldHdlZW4gPSBmdW5jdGlvbiAoZGF0ZTEsIGRhdGUyKSB7XG4gICAgICAgIC8vIFRoZSBudW1iZXIgb2YgbWlsbGlzZWNvbmRzIGluIG9uZSBkYXlcbiAgICAgICAgLy8gQ29udmVydCBib3RoIGRhdGVzIHRvIG1pbGxpc2Vjb25kc1xuICAgICAgICB2YXIgZGF0ZTFtcyA9IGRhdGUxLmdldFRpbWUoKSAtIGRhdGV1dGlsLnR6T2Zmc2V0KGRhdGUxKTtcbiAgICAgICAgdmFyIGRhdGUybXMgPSBkYXRlMi5nZXRUaW1lKCkgLSBkYXRldXRpbC50ek9mZnNldChkYXRlMik7XG4gICAgICAgIC8vIENhbGN1bGF0ZSB0aGUgZGlmZmVyZW5jZSBpbiBtaWxsaXNlY29uZHNcbiAgICAgICAgdmFyIGRpZmZlcmVuY2VtcyA9IGRhdGUxbXMgLSBkYXRlMm1zO1xuICAgICAgICAvLyBDb252ZXJ0IGJhY2sgdG8gZGF5cyBhbmQgcmV0dXJuXG4gICAgICAgIHJldHVybiBNYXRoLnJvdW5kKGRpZmZlcmVuY2VtcyAvIGRhdGV1dGlsLk9ORV9EQVkpO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogQHNlZTogPGh0dHA6Ly9kb2NzLnB5dGhvbi5vcmcvbGlicmFyeS9kYXRldGltZS5odG1sI2RhdGV0aW1lLmRhdGUudG9vcmRpbmFsPlxuICAgICAqL1xuICAgIGRhdGV1dGlsLnRvT3JkaW5hbCA9IGZ1bmN0aW9uIChkYXRlKSB7XG4gICAgICAgIHJldHVybiBkYXRldXRpbC5kYXlzQmV0d2VlbihkYXRlLCBkYXRldXRpbC5PUkRJTkFMX0JBU0UpO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogQHNlZSAtIDxodHRwOi8vZG9jcy5weXRob24ub3JnL2xpYnJhcnkvZGF0ZXRpbWUuaHRtbCNkYXRldGltZS5kYXRlLmZyb21vcmRpbmFsPlxuICAgICAqL1xuICAgIGRhdGV1dGlsLmZyb21PcmRpbmFsID0gZnVuY3Rpb24gKG9yZGluYWwpIHtcbiAgICAgICAgcmV0dXJuIG5ldyBEYXRlKGRhdGV1dGlsLk9SRElOQUxfQkFTRS5nZXRUaW1lKCkgKyBvcmRpbmFsICogZGF0ZXV0aWwuT05FX0RBWSk7XG4gICAgfTtcbiAgICBkYXRldXRpbC5nZXRNb250aERheXMgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICB2YXIgbW9udGggPSBkYXRlLmdldFVUQ01vbnRoKCk7XG4gICAgICAgIHJldHVybiBtb250aCA9PT0gMSAmJiBkYXRldXRpbC5pc0xlYXBZZWFyKGRhdGUuZ2V0VVRDRnVsbFllYXIoKSlcbiAgICAgICAgICAgID8gMjlcbiAgICAgICAgICAgIDogZGF0ZXV0aWwuTU9OVEhfREFZU1ttb250aF07XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBAcmV0dXJuIHtOdW1iZXJ9IHB5dGhvbi1saWtlIHdlZWtkYXlcbiAgICAgKi9cbiAgICBkYXRldXRpbC5nZXRXZWVrZGF5ID0gZnVuY3Rpb24gKGRhdGUpIHtcbiAgICAgICAgcmV0dXJuIGRhdGV1dGlsLlBZX1dFRUtEQVlTW2RhdGUuZ2V0VVRDRGF5KCldO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogQHNlZTogPGh0dHA6Ly9kb2NzLnB5dGhvbi5vcmcvbGlicmFyeS9jYWxlbmRhci5odG1sI2NhbGVuZGFyLm1vbnRocmFuZ2U+XG4gICAgICovXG4gICAgZGF0ZXV0aWwubW9udGhSYW5nZSA9IGZ1bmN0aW9uICh5ZWFyLCBtb250aCkge1xuICAgICAgICB2YXIgZGF0ZSA9IG5ldyBEYXRlKERhdGUuVVRDKHllYXIsIG1vbnRoLCAxKSk7XG4gICAgICAgIHJldHVybiBbZGF0ZXV0aWwuZ2V0V2Vla2RheShkYXRlKSwgZGF0ZXV0aWwuZ2V0TW9udGhEYXlzKGRhdGUpXTtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEBzZWU6IDxodHRwOi8vZG9jcy5weXRob24ub3JnL2xpYnJhcnkvZGF0ZXRpbWUuaHRtbCNkYXRldGltZS5kYXRldGltZS5jb21iaW5lPlxuICAgICAqL1xuICAgIGRhdGV1dGlsLmNvbWJpbmUgPSBmdW5jdGlvbiAoZGF0ZSwgdGltZSkge1xuICAgICAgICB0aW1lID0gdGltZSB8fCBkYXRlO1xuICAgICAgICByZXR1cm4gbmV3IERhdGUoRGF0ZS5VVEMoZGF0ZS5nZXRVVENGdWxsWWVhcigpLCBkYXRlLmdldFVUQ01vbnRoKCksIGRhdGUuZ2V0VVRDRGF0ZSgpLCB0aW1lLmdldEhvdXJzKCksIHRpbWUuZ2V0TWludXRlcygpLCB0aW1lLmdldFNlY29uZHMoKSwgdGltZS5nZXRNaWxsaXNlY29uZHMoKSkpO1xuICAgIH07XG4gICAgZGF0ZXV0aWwuY2xvbmUgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICB2YXIgZG9sbHkgPSBuZXcgRGF0ZShkYXRlLmdldFRpbWUoKSk7XG4gICAgICAgIHJldHVybiBkb2xseTtcbiAgICB9O1xuICAgIGRhdGV1dGlsLmNsb25lRGF0ZXMgPSBmdW5jdGlvbiAoZGF0ZXMpIHtcbiAgICAgICAgdmFyIGNsb25lcyA9IFtdO1xuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGRhdGVzLmxlbmd0aDsgaSsrKSB7XG4gICAgICAgICAgICBjbG9uZXMucHVzaChkYXRldXRpbC5jbG9uZShkYXRlc1tpXSkpO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiBjbG9uZXM7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBTb3J0cyBhbiBhcnJheSBvZiBEYXRlIG9yIGRhdGV1dGlsLlRpbWUgb2JqZWN0c1xuICAgICAqL1xuICAgIGRhdGV1dGlsLnNvcnQgPSBmdW5jdGlvbiAoZGF0ZXMpIHtcbiAgICAgICAgZGF0ZXMuc29ydChmdW5jdGlvbiAoYSwgYikge1xuICAgICAgICAgICAgcmV0dXJuIGEuZ2V0VGltZSgpIC0gYi5nZXRUaW1lKCk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgZGF0ZXV0aWwudGltZVRvVW50aWxTdHJpbmcgPSBmdW5jdGlvbiAodGltZSwgdXRjKSB7XG4gICAgICAgIGlmICh1dGMgPT09IHZvaWQgMCkgeyB1dGMgPSB0cnVlOyB9XG4gICAgICAgIHZhciBkYXRlID0gbmV3IERhdGUodGltZSk7XG4gICAgICAgIHJldHVybiBbXG4gICAgICAgICAgICBwYWRTdGFydChkYXRlLmdldFVUQ0Z1bGxZZWFyKCkudG9TdHJpbmcoKSwgNCwgJzAnKSxcbiAgICAgICAgICAgIHBhZFN0YXJ0KGRhdGUuZ2V0VVRDTW9udGgoKSArIDEsIDIsICcwJyksXG4gICAgICAgICAgICBwYWRTdGFydChkYXRlLmdldFVUQ0RhdGUoKSwgMiwgJzAnKSxcbiAgICAgICAgICAgICdUJyxcbiAgICAgICAgICAgIHBhZFN0YXJ0KGRhdGUuZ2V0VVRDSG91cnMoKSwgMiwgJzAnKSxcbiAgICAgICAgICAgIHBhZFN0YXJ0KGRhdGUuZ2V0VVRDTWludXRlcygpLCAyLCAnMCcpLFxuICAgICAgICAgICAgcGFkU3RhcnQoZGF0ZS5nZXRVVENTZWNvbmRzKCksIDIsICcwJyksXG4gICAgICAgICAgICB1dGMgPyAnWicgOiAnJyxcbiAgICAgICAgXS5qb2luKCcnKTtcbiAgICB9O1xuICAgIGRhdGV1dGlsLnVudGlsU3RyaW5nVG9EYXRlID0gZnVuY3Rpb24gKHVudGlsKSB7XG4gICAgICAgIHZhciByZSA9IC9eKFxcZHs0fSkoXFxkezJ9KShcXGR7Mn0pKFQoXFxkezJ9KShcXGR7Mn0pKFxcZHsyfSlaPyk/JC87XG4gICAgICAgIHZhciBiaXRzID0gcmUuZXhlYyh1bnRpbCk7XG4gICAgICAgIGlmICghYml0cylcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihcIkludmFsaWQgVU5USUwgdmFsdWU6IFwiLmNvbmNhdCh1bnRpbCkpO1xuICAgICAgICByZXR1cm4gbmV3IERhdGUoRGF0ZS5VVEMocGFyc2VJbnQoYml0c1sxXSwgMTApLCBwYXJzZUludChiaXRzWzJdLCAxMCkgLSAxLCBwYXJzZUludChiaXRzWzNdLCAxMCksIHBhcnNlSW50KGJpdHNbNV0sIDEwKSB8fCAwLCBwYXJzZUludChiaXRzWzZdLCAxMCkgfHwgMCwgcGFyc2VJbnQoYml0c1s3XSwgMTApIHx8IDApKTtcbiAgICB9O1xufSkoZGF0ZXV0aWwgfHwgKGRhdGV1dGlsID0ge30pKTtcbmV4cG9ydCBkZWZhdWx0IGRhdGV1dGlsO1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9ZGF0ZXV0aWwuanMubWFwIiwiaW1wb3J0IGRhdGV1dGlsIGZyb20gJy4vZGF0ZXV0aWwnO1xudmFyIERhdGVXaXRoWm9uZSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBEYXRlV2l0aFpvbmUoZGF0ZSwgdHppZCkge1xuICAgICAgICBpZiAoaXNOYU4oZGF0ZS5nZXRUaW1lKCkpKSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgUmFuZ2VFcnJvcignSW52YWxpZCBkYXRlIHBhc3NlZCB0byBEYXRlV2l0aFpvbmUnKTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLmRhdGUgPSBkYXRlO1xuICAgICAgICB0aGlzLnR6aWQgPSB0emlkO1xuICAgIH1cbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkoRGF0ZVdpdGhab25lLnByb3RvdHlwZSwgXCJpc1VUQ1wiLCB7XG4gICAgICAgIGdldDogZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgcmV0dXJuICF0aGlzLnR6aWQgfHwgdGhpcy50emlkLnRvVXBwZXJDYXNlKCkgPT09ICdVVEMnO1xuICAgICAgICB9LFxuICAgICAgICBlbnVtZXJhYmxlOiBmYWxzZSxcbiAgICAgICAgY29uZmlndXJhYmxlOiB0cnVlXG4gICAgfSk7XG4gICAgRGF0ZVdpdGhab25lLnByb3RvdHlwZS50b1N0cmluZyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIGRhdGVzdHIgPSBkYXRldXRpbC50aW1lVG9VbnRpbFN0cmluZyh0aGlzLmRhdGUuZ2V0VGltZSgpLCB0aGlzLmlzVVRDKTtcbiAgICAgICAgaWYgKCF0aGlzLmlzVVRDKSB7XG4gICAgICAgICAgICByZXR1cm4gXCI7VFpJRD1cIi5jb25jYXQodGhpcy50emlkLCBcIjpcIikuY29uY2F0KGRhdGVzdHIpO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiBcIjpcIi5jb25jYXQoZGF0ZXN0cik7XG4gICAgfTtcbiAgICBEYXRlV2l0aFpvbmUucHJvdG90eXBlLmdldFRpbWUgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmRhdGUuZ2V0VGltZSgpO1xuICAgIH07XG4gICAgRGF0ZVdpdGhab25lLnByb3RvdHlwZS5yZXpvbmVkRGF0ZSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIF9hO1xuICAgICAgICBpZiAodGhpcy5pc1VUQykge1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMuZGF0ZTtcbiAgICAgICAgfVxuICAgICAgICB2YXIgbG9jYWxUaW1lWm9uZSA9IEludGwuRGF0ZVRpbWVGb3JtYXQoKS5yZXNvbHZlZE9wdGlvbnMoKS50aW1lWm9uZTtcbiAgICAgICAgdmFyIGRhdGVJbkxvY2FsVFogPSBuZXcgRGF0ZSh0aGlzLmRhdGUudG9Mb2NhbGVTdHJpbmcodW5kZWZpbmVkLCB7IHRpbWVab25lOiBsb2NhbFRpbWVab25lIH0pKTtcbiAgICAgICAgdmFyIGRhdGVJblRhcmdldFRaID0gbmV3IERhdGUodGhpcy5kYXRlLnRvTG9jYWxlU3RyaW5nKHVuZGVmaW5lZCwgeyB0aW1lWm9uZTogKF9hID0gdGhpcy50emlkKSAhPT0gbnVsbCAmJiBfYSAhPT0gdm9pZCAwID8gX2EgOiAnVVRDJyB9KSk7XG4gICAgICAgIHZhciB0ek9mZnNldCA9IGRhdGVJblRhcmdldFRaLmdldFRpbWUoKSAtIGRhdGVJbkxvY2FsVFouZ2V0VGltZSgpO1xuICAgICAgICByZXR1cm4gbmV3IERhdGUodGhpcy5kYXRlLmdldFRpbWUoKSAtIHR6T2Zmc2V0KTtcbiAgICB9O1xuICAgIHJldHVybiBEYXRlV2l0aFpvbmU7XG59KCkpO1xuZXhwb3J0IHsgRGF0ZVdpdGhab25lIH07XG4vLyMgc291cmNlTWFwcGluZ1VSTD1kYXRld2l0aHpvbmUuanMubWFwIiwiLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbi8vIEhlbHBlciBmdW5jdGlvbnNcbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG5pbXBvcnQgeyBBTExfV0VFS0RBWVMgfSBmcm9tICcuL3dlZWtkYXknO1xuZXhwb3J0IHZhciBpc1ByZXNlbnQgPSBmdW5jdGlvbiAodmFsdWUpIHtcbiAgICByZXR1cm4gdmFsdWUgIT09IG51bGwgJiYgdmFsdWUgIT09IHVuZGVmaW5lZDtcbn07XG5leHBvcnQgdmFyIGlzTnVtYmVyID0gZnVuY3Rpb24gKHZhbHVlKSB7XG4gICAgcmV0dXJuIHR5cGVvZiB2YWx1ZSA9PT0gJ251bWJlcic7XG59O1xuZXhwb3J0IHZhciBpc1dlZWtkYXlTdHIgPSBmdW5jdGlvbiAodmFsdWUpIHtcbiAgICByZXR1cm4gdHlwZW9mIHZhbHVlID09PSAnc3RyaW5nJyAmJiBBTExfV0VFS0RBWVMuaW5jbHVkZXModmFsdWUpO1xufTtcbmV4cG9ydCB2YXIgaXNBcnJheSA9IEFycmF5LmlzQXJyYXk7XG4vKipcbiAqIFNpbXBsaWZpZWQgdmVyc2lvbiBvZiBweXRob24ncyByYW5nZSgpXG4gKi9cbmV4cG9ydCB2YXIgcmFuZ2UgPSBmdW5jdGlvbiAoc3RhcnQsIGVuZCkge1xuICAgIGlmIChlbmQgPT09IHZvaWQgMCkgeyBlbmQgPSBzdGFydDsgfVxuICAgIGlmIChhcmd1bWVudHMubGVuZ3RoID09PSAxKSB7XG4gICAgICAgIGVuZCA9IHN0YXJ0O1xuICAgICAgICBzdGFydCA9IDA7XG4gICAgfVxuICAgIHZhciByYW5nID0gW107XG4gICAgZm9yICh2YXIgaSA9IHN0YXJ0OyBpIDwgZW5kOyBpKyspXG4gICAgICAgIHJhbmcucHVzaChpKTtcbiAgICByZXR1cm4gcmFuZztcbn07XG5leHBvcnQgdmFyIGNsb25lID0gZnVuY3Rpb24gKGFycmF5KSB7XG4gICAgcmV0dXJuIFtdLmNvbmNhdChhcnJheSk7XG59O1xuZXhwb3J0IHZhciByZXBlYXQgPSBmdW5jdGlvbiAodmFsdWUsIHRpbWVzKSB7XG4gICAgdmFyIGkgPSAwO1xuICAgIHZhciBhcnJheSA9IFtdO1xuICAgIGlmIChpc0FycmF5KHZhbHVlKSkge1xuICAgICAgICBmb3IgKDsgaSA8IHRpbWVzOyBpKyspXG4gICAgICAgICAgICBhcnJheVtpXSA9IFtdLmNvbmNhdCh2YWx1ZSk7XG4gICAgfVxuICAgIGVsc2Uge1xuICAgICAgICBmb3IgKDsgaSA8IHRpbWVzOyBpKyspXG4gICAgICAgICAgICBhcnJheVtpXSA9IHZhbHVlO1xuICAgIH1cbiAgICByZXR1cm4gYXJyYXk7XG59O1xuZXhwb3J0IHZhciB0b0FycmF5ID0gZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICBpZiAoaXNBcnJheShpdGVtKSkge1xuICAgICAgICByZXR1cm4gaXRlbTtcbiAgICB9XG4gICAgcmV0dXJuIFtpdGVtXTtcbn07XG5leHBvcnQgZnVuY3Rpb24gcGFkU3RhcnQoaXRlbSwgdGFyZ2V0TGVuZ3RoLCBwYWRTdHJpbmcpIHtcbiAgICBpZiAocGFkU3RyaW5nID09PSB2b2lkIDApIHsgcGFkU3RyaW5nID0gJyAnOyB9XG4gICAgdmFyIHN0ciA9IFN0cmluZyhpdGVtKTtcbiAgICB0YXJnZXRMZW5ndGggPSB0YXJnZXRMZW5ndGggPj4gMDtcbiAgICBpZiAoc3RyLmxlbmd0aCA+IHRhcmdldExlbmd0aCkge1xuICAgICAgICByZXR1cm4gU3RyaW5nKHN0cik7XG4gICAgfVxuICAgIHRhcmdldExlbmd0aCA9IHRhcmdldExlbmd0aCAtIHN0ci5sZW5ndGg7XG4gICAgaWYgKHRhcmdldExlbmd0aCA+IHBhZFN0cmluZy5sZW5ndGgpIHtcbiAgICAgICAgcGFkU3RyaW5nICs9IHJlcGVhdChwYWRTdHJpbmcsIHRhcmdldExlbmd0aCAvIHBhZFN0cmluZy5sZW5ndGgpO1xuICAgIH1cbiAgICByZXR1cm4gcGFkU3RyaW5nLnNsaWNlKDAsIHRhcmdldExlbmd0aCkgKyBTdHJpbmcoc3RyKTtcbn1cbi8qKlxuICogUHl0aG9uIGxpa2Ugc3BsaXRcbiAqL1xuZXhwb3J0IHZhciBzcGxpdCA9IGZ1bmN0aW9uIChzdHIsIHNlcCwgbnVtKSB7XG4gICAgdmFyIHNwbGl0cyA9IHN0ci5zcGxpdChzZXApO1xuICAgIHJldHVybiBudW1cbiAgICAgICAgPyBzcGxpdHMuc2xpY2UoMCwgbnVtKS5jb25jYXQoW3NwbGl0cy5zbGljZShudW0pLmpvaW4oc2VwKV0pXG4gICAgICAgIDogc3BsaXRzO1xufTtcbi8qKlxuICogY2xvc3VyZS9nb29nL21hdGgvbWF0aC5qczptb2R1bG9cbiAqIENvcHlyaWdodCAyMDA2IFRoZSBDbG9zdXJlIExpYnJhcnkgQXV0aG9ycy5cbiAqIFRoZSAlIG9wZXJhdG9yIGluIEphdmFTY3JpcHQgcmV0dXJucyB0aGUgcmVtYWluZGVyIG9mIGEgLyBiLCBidXQgZGlmZmVycyBmcm9tXG4gKiBzb21lIG90aGVyIGxhbmd1YWdlcyBpbiB0aGF0IHRoZSByZXN1bHQgd2lsbCBoYXZlIHRoZSBzYW1lIHNpZ24gYXMgdGhlXG4gKiBkaXZpZGVuZC4gRm9yIGV4YW1wbGUsIC0xICUgOCA9PSAtMSwgd2hlcmVhcyBpbiBzb21lIG90aGVyIGxhbmd1YWdlc1xuICogKHN1Y2ggYXMgUHl0aG9uKSB0aGUgcmVzdWx0IHdvdWxkIGJlIDcuIFRoaXMgZnVuY3Rpb24gZW11bGF0ZXMgdGhlIG1vcmVcbiAqIGNvcnJlY3QgbW9kdWxvIGJlaGF2aW9yLCB3aGljaCBpcyB1c2VmdWwgZm9yIGNlcnRhaW4gYXBwbGljYXRpb25zIHN1Y2ggYXNcbiAqIGNhbGN1bGF0aW5nIGFuIG9mZnNldCBpbmRleCBpbiBhIGNpcmN1bGFyIGxpc3QuXG4gKlxuICogQHBhcmFtIHtudW1iZXJ9IGEgVGhlIGRpdmlkZW5kLlxuICogQHBhcmFtIHtudW1iZXJ9IGIgVGhlIGRpdmlzb3IuXG4gKiBAcmV0dXJuIHtudW1iZXJ9IGEgJSBiIHdoZXJlIHRoZSByZXN1bHQgaXMgYmV0d2VlbiAwIGFuZCBiIChlaXRoZXIgMCA8PSB4IDwgYlxuICogb3IgYiA8IHggPD0gMCwgZGVwZW5kaW5nIG9uIHRoZSBzaWduIG9mIGIpLlxuICovXG5leHBvcnQgdmFyIHB5bW9kID0gZnVuY3Rpb24gKGEsIGIpIHtcbiAgICB2YXIgciA9IGEgJSBiO1xuICAgIC8vIElmIHIgYW5kIGIgZGlmZmVyIGluIHNpZ24sIGFkZCBiIHRvIHdyYXAgdGhlIHJlc3VsdCB0byB0aGUgY29ycmVjdCBzaWduLlxuICAgIHJldHVybiByICogYiA8IDAgPyByICsgYiA6IHI7XG59O1xuLyoqXG4gKiBAc2VlOiA8aHR0cDovL2RvY3MucHl0aG9uLm9yZy9saWJyYXJ5L2Z1bmN0aW9ucy5odG1sI2Rpdm1vZD5cbiAqL1xuZXhwb3J0IHZhciBkaXZtb2QgPSBmdW5jdGlvbiAoYSwgYikge1xuICAgIHJldHVybiB7IGRpdjogTWF0aC5mbG9vcihhIC8gYiksIG1vZDogcHltb2QoYSwgYikgfTtcbn07XG5leHBvcnQgdmFyIGVtcHR5ID0gZnVuY3Rpb24gKG9iaikge1xuICAgIHJldHVybiAhaXNQcmVzZW50KG9iaikgfHwgb2JqLmxlbmd0aCA9PT0gMDtcbn07XG4vKipcbiAqIFB5dGhvbi1saWtlIGJvb2xlYW5cbiAqXG4gKiBAcmV0dXJuIHtCb29sZWFufSB2YWx1ZSBvZiBhbiBvYmplY3QvcHJpbWl0aXZlLCB0YWtpbmcgaW50byBhY2NvdW50XG4gKiB0aGUgZmFjdCB0aGF0IGluIFB5dGhvbiBhbiBlbXB0eSBsaXN0J3MvdHVwbGUnc1xuICogYm9vbGVhbiB2YWx1ZSBpcyBGYWxzZSwgd2hlcmVhcyBpbiBKUyBpdCdzIHRydWVcbiAqL1xuZXhwb3J0IHZhciBub3RFbXB0eSA9IGZ1bmN0aW9uIChvYmopIHtcbiAgICByZXR1cm4gIWVtcHR5KG9iaik7XG59O1xuLyoqXG4gKiBSZXR1cm4gdHJ1ZSBpZiBhIHZhbHVlIGlzIGluIGFuIGFycmF5XG4gKi9cbmV4cG9ydCB2YXIgaW5jbHVkZXMgPSBmdW5jdGlvbiAoYXJyLCB2YWwpIHtcbiAgICByZXR1cm4gbm90RW1wdHkoYXJyKSAmJiBhcnIuaW5kZXhPZih2YWwpICE9PSAtMTtcbn07XG4vLyMgc291cmNlTWFwcGluZ1VSTD1oZWxwZXJzLmpzLm1hcCIsIi8qICFcbiAqIHJydWxlLmpzIC0gTGlicmFyeSBmb3Igd29ya2luZyB3aXRoIHJlY3VycmVuY2UgcnVsZXMgZm9yIGNhbGVuZGFyIGRhdGVzLlxuICogaHR0cHM6Ly9naXRodWIuY29tL2pha3Vicm96dG9jaWwvcnJ1bGVcbiAqXG4gKiBDb3B5cmlnaHQgMjAxMCwgSmFrdWIgUm96dG9jaWwgYW5kIExhcnMgU2Nob25pbmdcbiAqIExpY2VuY2VkIHVuZGVyIHRoZSBCU0QgbGljZW5jZS5cbiAqIGh0dHBzOi8vZ2l0aHViLmNvbS9qYWt1YnJvenRvY2lsL3JydWxlL2Jsb2IvbWFzdGVyL0xJQ0VOQ0VcbiAqXG4gKiBCYXNlZCBvbjpcbiAqIHB5dGhvbi1kYXRldXRpbCAtIEV4dGVuc2lvbnMgdG8gdGhlIHN0YW5kYXJkIFB5dGhvbiBkYXRldGltZSBtb2R1bGUuXG4gKiBDb3B5cmlnaHQgKGMpIDIwMDMtMjAxMSAtIEd1c3Rhdm8gTmllbWV5ZXIgPGd1c3Rhdm9AbmllbWV5ZXIubmV0PlxuICogQ29weXJpZ2h0IChjKSAyMDEyIC0gVG9taSBQaWV2aWzDpGluZW4gPHRvbWkucGlldmlsYWluZW5AaWtpLmZpPlxuICogaHR0cHM6Ly9naXRodWIuY29tL2pha3Vicm96dG9jaWwvcnJ1bGUvYmxvYi9tYXN0ZXIvTElDRU5DRVxuICpcbiAqL1xuZXhwb3J0IHsgUlJ1bGUgfSBmcm9tICcuL3JydWxlJztcbmV4cG9ydCB7IFJSdWxlU2V0IH0gZnJvbSAnLi9ycnVsZXNldCc7XG5leHBvcnQgeyBycnVsZXN0ciB9IGZyb20gJy4vcnJ1bGVzdHInO1xuZXhwb3J0IHsgRnJlcXVlbmN5IH0gZnJvbSAnLi90eXBlcyc7XG5leHBvcnQgeyBXZWVrZGF5IH0gZnJvbSAnLi93ZWVrZGF5Jztcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPWluZGV4LmpzLm1hcCIsImltcG9ydCB7IGZyZXFJc0RhaWx5T3JHcmVhdGVyIH0gZnJvbSAnLi4vdHlwZXMnO1xuaW1wb3J0IGRhdGV1dGlsIGZyb20gJy4uL2RhdGV1dGlsJztcbmltcG9ydCBJdGVyaW5mbyBmcm9tICcuLi9pdGVyaW5mby9pbmRleCc7XG5pbXBvcnQgeyBSUnVsZSB9IGZyb20gJy4uL3JydWxlJztcbmltcG9ydCB7IGJ1aWxkVGltZXNldCB9IGZyb20gJy4uL3BhcnNlb3B0aW9ucyc7XG5pbXBvcnQgeyBub3RFbXB0eSwgaW5jbHVkZXMsIGlzUHJlc2VudCB9IGZyb20gJy4uL2hlbHBlcnMnO1xuaW1wb3J0IHsgRGF0ZVdpdGhab25lIH0gZnJvbSAnLi4vZGF0ZXdpdGh6b25lJztcbmltcG9ydCB7IGJ1aWxkUG9zbGlzdCB9IGZyb20gJy4vcG9zbGlzdCc7XG5pbXBvcnQgeyBEYXRlVGltZSB9IGZyb20gJy4uL2RhdGV0aW1lJztcbmV4cG9ydCBmdW5jdGlvbiBpdGVyKGl0ZXJSZXN1bHQsIG9wdGlvbnMpIHtcbiAgICB2YXIgZHRzdGFydCA9IG9wdGlvbnMuZHRzdGFydCwgZnJlcSA9IG9wdGlvbnMuZnJlcSwgaW50ZXJ2YWwgPSBvcHRpb25zLmludGVydmFsLCB1bnRpbCA9IG9wdGlvbnMudW50aWwsIGJ5c2V0cG9zID0gb3B0aW9ucy5ieXNldHBvcztcbiAgICB2YXIgY291bnQgPSBvcHRpb25zLmNvdW50O1xuICAgIGlmIChjb3VudCA9PT0gMCB8fCBpbnRlcnZhbCA9PT0gMCkge1xuICAgICAgICByZXR1cm4gZW1pdFJlc3VsdChpdGVyUmVzdWx0KTtcbiAgICB9XG4gICAgdmFyIGNvdW50ZXJEYXRlID0gRGF0ZVRpbWUuZnJvbURhdGUoZHRzdGFydCk7XG4gICAgdmFyIGlpID0gbmV3IEl0ZXJpbmZvKG9wdGlvbnMpO1xuICAgIGlpLnJlYnVpbGQoY291bnRlckRhdGUueWVhciwgY291bnRlckRhdGUubW9udGgpO1xuICAgIHZhciB0aW1lc2V0ID0gbWFrZVRpbWVzZXQoaWksIGNvdW50ZXJEYXRlLCBvcHRpb25zKTtcbiAgICBmb3IgKDs7KSB7XG4gICAgICAgIHZhciBfYSA9IGlpLmdldGRheXNldChmcmVxKShjb3VudGVyRGF0ZS55ZWFyLCBjb3VudGVyRGF0ZS5tb250aCwgY291bnRlckRhdGUuZGF5KSwgZGF5c2V0ID0gX2FbMF0sIHN0YXJ0ID0gX2FbMV0sIGVuZCA9IF9hWzJdO1xuICAgICAgICB2YXIgZmlsdGVyZWQgPSByZW1vdmVGaWx0ZXJlZERheXMoZGF5c2V0LCBzdGFydCwgZW5kLCBpaSwgb3B0aW9ucyk7XG4gICAgICAgIGlmIChub3RFbXB0eShieXNldHBvcykpIHtcbiAgICAgICAgICAgIHZhciBwb3NsaXN0ID0gYnVpbGRQb3NsaXN0KGJ5c2V0cG9zLCB0aW1lc2V0LCBzdGFydCwgZW5kLCBpaSwgZGF5c2V0KTtcbiAgICAgICAgICAgIGZvciAodmFyIGogPSAwOyBqIDwgcG9zbGlzdC5sZW5ndGg7IGorKykge1xuICAgICAgICAgICAgICAgIHZhciByZXMgPSBwb3NsaXN0W2pdO1xuICAgICAgICAgICAgICAgIGlmICh1bnRpbCAmJiByZXMgPiB1bnRpbCkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gZW1pdFJlc3VsdChpdGVyUmVzdWx0KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKHJlcyA+PSBkdHN0YXJ0KSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciByZXpvbmVkRGF0ZSA9IHJlem9uZUlmTmVlZGVkKHJlcywgb3B0aW9ucyk7XG4gICAgICAgICAgICAgICAgICAgIGlmICghaXRlclJlc3VsdC5hY2NlcHQocmV6b25lZERhdGUpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gZW1pdFJlc3VsdChpdGVyUmVzdWx0KTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBpZiAoY291bnQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIC0tY291bnQ7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoIWNvdW50KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGVtaXRSZXN1bHQoaXRlclJlc3VsdCk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBmb3IgKHZhciBqID0gc3RhcnQ7IGogPCBlbmQ7IGorKykge1xuICAgICAgICAgICAgICAgIHZhciBjdXJyZW50RGF5ID0gZGF5c2V0W2pdO1xuICAgICAgICAgICAgICAgIGlmICghaXNQcmVzZW50KGN1cnJlbnREYXkpKSB7XG4gICAgICAgICAgICAgICAgICAgIGNvbnRpbnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB2YXIgZGF0ZSA9IGRhdGV1dGlsLmZyb21PcmRpbmFsKGlpLnllYXJvcmRpbmFsICsgY3VycmVudERheSk7XG4gICAgICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCB0aW1lc2V0Lmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciB0aW1lID0gdGltZXNldFtrXTtcbiAgICAgICAgICAgICAgICAgICAgdmFyIHJlcyA9IGRhdGV1dGlsLmNvbWJpbmUoZGF0ZSwgdGltZSk7XG4gICAgICAgICAgICAgICAgICAgIGlmICh1bnRpbCAmJiByZXMgPiB1bnRpbCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGVtaXRSZXN1bHQoaXRlclJlc3VsdCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgaWYgKHJlcyA+PSBkdHN0YXJ0KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgcmV6b25lZERhdGUgPSByZXpvbmVJZk5lZWRlZChyZXMsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCFpdGVyUmVzdWx0LmFjY2VwdChyZXpvbmVkRGF0ZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gZW1pdFJlc3VsdChpdGVyUmVzdWx0KTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChjb3VudCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIC0tY291bnQ7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCFjb3VudCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gZW1pdFJlc3VsdChpdGVyUmVzdWx0KTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgaWYgKG9wdGlvbnMuaW50ZXJ2YWwgPT09IDApIHtcbiAgICAgICAgICAgIHJldHVybiBlbWl0UmVzdWx0KGl0ZXJSZXN1bHQpO1xuICAgICAgICB9XG4gICAgICAgIC8vIEhhbmRsZSBmcmVxdWVuY3kgYW5kIGludGVydmFsXG4gICAgICAgIGNvdW50ZXJEYXRlLmFkZChvcHRpb25zLCBmaWx0ZXJlZCk7XG4gICAgICAgIGlmIChjb3VudGVyRGF0ZS55ZWFyID4gZGF0ZXV0aWwuTUFYWUVBUikge1xuICAgICAgICAgICAgcmV0dXJuIGVtaXRSZXN1bHQoaXRlclJlc3VsdCk7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKCFmcmVxSXNEYWlseU9yR3JlYXRlcihmcmVxKSkge1xuICAgICAgICAgICAgdGltZXNldCA9IGlpLmdldHRpbWVzZXQoZnJlcSkoY291bnRlckRhdGUuaG91ciwgY291bnRlckRhdGUubWludXRlLCBjb3VudGVyRGF0ZS5zZWNvbmQsIDApO1xuICAgICAgICB9XG4gICAgICAgIGlpLnJlYnVpbGQoY291bnRlckRhdGUueWVhciwgY291bnRlckRhdGUubW9udGgpO1xuICAgIH1cbn1cbmZ1bmN0aW9uIGlzRmlsdGVyZWQoaWksIGN1cnJlbnREYXksIG9wdGlvbnMpIHtcbiAgICB2YXIgYnltb250aCA9IG9wdGlvbnMuYnltb250aCwgYnl3ZWVrbm8gPSBvcHRpb25zLmJ5d2Vla25vLCBieXdlZWtkYXkgPSBvcHRpb25zLmJ5d2Vla2RheSwgYnllYXN0ZXIgPSBvcHRpb25zLmJ5ZWFzdGVyLCBieW1vbnRoZGF5ID0gb3B0aW9ucy5ieW1vbnRoZGF5LCBieW5tb250aGRheSA9IG9wdGlvbnMuYnlubW9udGhkYXksIGJ5eWVhcmRheSA9IG9wdGlvbnMuYnl5ZWFyZGF5O1xuICAgIHJldHVybiAoKG5vdEVtcHR5KGJ5bW9udGgpICYmICFpbmNsdWRlcyhieW1vbnRoLCBpaS5tbWFza1tjdXJyZW50RGF5XSkpIHx8XG4gICAgICAgIChub3RFbXB0eShieXdlZWtubykgJiYgIWlpLndub21hc2tbY3VycmVudERheV0pIHx8XG4gICAgICAgIChub3RFbXB0eShieXdlZWtkYXkpICYmICFpbmNsdWRlcyhieXdlZWtkYXksIGlpLndkYXltYXNrW2N1cnJlbnREYXldKSkgfHxcbiAgICAgICAgKG5vdEVtcHR5KGlpLm53ZGF5bWFzaykgJiYgIWlpLm53ZGF5bWFza1tjdXJyZW50RGF5XSkgfHxcbiAgICAgICAgKGJ5ZWFzdGVyICE9PSBudWxsICYmICFpbmNsdWRlcyhpaS5lYXN0ZXJtYXNrLCBjdXJyZW50RGF5KSkgfHxcbiAgICAgICAgKChub3RFbXB0eShieW1vbnRoZGF5KSB8fCBub3RFbXB0eShieW5tb250aGRheSkpICYmXG4gICAgICAgICAgICAhaW5jbHVkZXMoYnltb250aGRheSwgaWkubWRheW1hc2tbY3VycmVudERheV0pICYmXG4gICAgICAgICAgICAhaW5jbHVkZXMoYnlubW9udGhkYXksIGlpLm5tZGF5bWFza1tjdXJyZW50RGF5XSkpIHx8XG4gICAgICAgIChub3RFbXB0eShieXllYXJkYXkpICYmXG4gICAgICAgICAgICAoKGN1cnJlbnREYXkgPCBpaS55ZWFybGVuICYmXG4gICAgICAgICAgICAgICAgIWluY2x1ZGVzKGJ5eWVhcmRheSwgY3VycmVudERheSArIDEpICYmXG4gICAgICAgICAgICAgICAgIWluY2x1ZGVzKGJ5eWVhcmRheSwgLWlpLnllYXJsZW4gKyBjdXJyZW50RGF5KSkgfHxcbiAgICAgICAgICAgICAgICAoY3VycmVudERheSA+PSBpaS55ZWFybGVuICYmXG4gICAgICAgICAgICAgICAgICAgICFpbmNsdWRlcyhieXllYXJkYXksIGN1cnJlbnREYXkgKyAxIC0gaWkueWVhcmxlbikgJiZcbiAgICAgICAgICAgICAgICAgICAgIWluY2x1ZGVzKGJ5eWVhcmRheSwgLWlpLm5leHR5ZWFybGVuICsgY3VycmVudERheSAtIGlpLnllYXJsZW4pKSkpKTtcbn1cbmZ1bmN0aW9uIHJlem9uZUlmTmVlZGVkKGRhdGUsIG9wdGlvbnMpIHtcbiAgICByZXR1cm4gbmV3IERhdGVXaXRoWm9uZShkYXRlLCBvcHRpb25zLnR6aWQpLnJlem9uZWREYXRlKCk7XG59XG5mdW5jdGlvbiBlbWl0UmVzdWx0KGl0ZXJSZXN1bHQpIHtcbiAgICByZXR1cm4gaXRlclJlc3VsdC5nZXRWYWx1ZSgpO1xufVxuZnVuY3Rpb24gcmVtb3ZlRmlsdGVyZWREYXlzKGRheXNldCwgc3RhcnQsIGVuZCwgaWksIG9wdGlvbnMpIHtcbiAgICB2YXIgZmlsdGVyZWQgPSBmYWxzZTtcbiAgICBmb3IgKHZhciBkYXlDb3VudGVyID0gc3RhcnQ7IGRheUNvdW50ZXIgPCBlbmQ7IGRheUNvdW50ZXIrKykge1xuICAgICAgICB2YXIgY3VycmVudERheSA9IGRheXNldFtkYXlDb3VudGVyXTtcbiAgICAgICAgZmlsdGVyZWQgPSBpc0ZpbHRlcmVkKGlpLCBjdXJyZW50RGF5LCBvcHRpb25zKTtcbiAgICAgICAgaWYgKGZpbHRlcmVkKVxuICAgICAgICAgICAgZGF5c2V0W2N1cnJlbnREYXldID0gbnVsbDtcbiAgICB9XG4gICAgcmV0dXJuIGZpbHRlcmVkO1xufVxuZnVuY3Rpb24gbWFrZVRpbWVzZXQoaWksIGNvdW50ZXJEYXRlLCBvcHRpb25zKSB7XG4gICAgdmFyIGZyZXEgPSBvcHRpb25zLmZyZXEsIGJ5aG91ciA9IG9wdGlvbnMuYnlob3VyLCBieW1pbnV0ZSA9IG9wdGlvbnMuYnltaW51dGUsIGJ5c2Vjb25kID0gb3B0aW9ucy5ieXNlY29uZDtcbiAgICBpZiAoZnJlcUlzRGFpbHlPckdyZWF0ZXIoZnJlcSkpIHtcbiAgICAgICAgcmV0dXJuIGJ1aWxkVGltZXNldChvcHRpb25zKTtcbiAgICB9XG4gICAgaWYgKChmcmVxID49IFJSdWxlLkhPVVJMWSAmJlxuICAgICAgICBub3RFbXB0eShieWhvdXIpICYmXG4gICAgICAgICFpbmNsdWRlcyhieWhvdXIsIGNvdW50ZXJEYXRlLmhvdXIpKSB8fFxuICAgICAgICAoZnJlcSA+PSBSUnVsZS5NSU5VVEVMWSAmJlxuICAgICAgICAgICAgbm90RW1wdHkoYnltaW51dGUpICYmXG4gICAgICAgICAgICAhaW5jbHVkZXMoYnltaW51dGUsIGNvdW50ZXJEYXRlLm1pbnV0ZSkpIHx8XG4gICAgICAgIChmcmVxID49IFJSdWxlLlNFQ09ORExZICYmXG4gICAgICAgICAgICBub3RFbXB0eShieXNlY29uZCkgJiZcbiAgICAgICAgICAgICFpbmNsdWRlcyhieXNlY29uZCwgY291bnRlckRhdGUuc2Vjb25kKSkpIHtcbiAgICAgICAgcmV0dXJuIFtdO1xuICAgIH1cbiAgICByZXR1cm4gaWkuZ2V0dGltZXNldChmcmVxKShjb3VudGVyRGF0ZS5ob3VyLCBjb3VudGVyRGF0ZS5taW51dGUsIGNvdW50ZXJEYXRlLnNlY29uZCwgY291bnRlckRhdGUubWlsbGlzZWNvbmQpO1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9aW5kZXguanMubWFwIiwiaW1wb3J0IGRhdGV1dGlsIGZyb20gJy4uL2RhdGV1dGlsJztcbmltcG9ydCB7IHB5bW9kLCBpc1ByZXNlbnQsIGluY2x1ZGVzIH0gZnJvbSAnLi4vaGVscGVycyc7XG5leHBvcnQgZnVuY3Rpb24gYnVpbGRQb3NsaXN0KGJ5c2V0cG9zLCB0aW1lc2V0LCBzdGFydCwgZW5kLCBpaSwgZGF5c2V0KSB7XG4gICAgdmFyIHBvc2xpc3QgPSBbXTtcbiAgICBmb3IgKHZhciBqID0gMDsgaiA8IGJ5c2V0cG9zLmxlbmd0aDsgaisrKSB7XG4gICAgICAgIHZhciBkYXlwb3MgPSB2b2lkIDA7XG4gICAgICAgIHZhciB0aW1lcG9zID0gdm9pZCAwO1xuICAgICAgICB2YXIgcG9zID0gYnlzZXRwb3Nbal07XG4gICAgICAgIGlmIChwb3MgPCAwKSB7XG4gICAgICAgICAgICBkYXlwb3MgPSBNYXRoLmZsb29yKHBvcyAvIHRpbWVzZXQubGVuZ3RoKTtcbiAgICAgICAgICAgIHRpbWVwb3MgPSBweW1vZChwb3MsIHRpbWVzZXQubGVuZ3RoKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGRheXBvcyA9IE1hdGguZmxvb3IoKHBvcyAtIDEpIC8gdGltZXNldC5sZW5ndGgpO1xuICAgICAgICAgICAgdGltZXBvcyA9IHB5bW9kKHBvcyAtIDEsIHRpbWVzZXQubGVuZ3RoKTtcbiAgICAgICAgfVxuICAgICAgICB2YXIgdG1wID0gW107XG4gICAgICAgIGZvciAodmFyIGsgPSBzdGFydDsgayA8IGVuZDsgaysrKSB7XG4gICAgICAgICAgICB2YXIgdmFsID0gZGF5c2V0W2tdO1xuICAgICAgICAgICAgaWYgKCFpc1ByZXNlbnQodmFsKSlcbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIHRtcC5wdXNoKHZhbCk7XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGkgPSB2b2lkIDA7XG4gICAgICAgIGlmIChkYXlwb3MgPCAwKSB7XG4gICAgICAgICAgICBpID0gdG1wLnNsaWNlKGRheXBvcylbMF07XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBpID0gdG1wW2RheXBvc107XG4gICAgICAgIH1cbiAgICAgICAgdmFyIHRpbWUgPSB0aW1lc2V0W3RpbWVwb3NdO1xuICAgICAgICB2YXIgZGF0ZSA9IGRhdGV1dGlsLmZyb21PcmRpbmFsKGlpLnllYXJvcmRpbmFsICsgaSk7XG4gICAgICAgIHZhciByZXMgPSBkYXRldXRpbC5jb21iaW5lKGRhdGUsIHRpbWUpO1xuICAgICAgICAvLyBYWFg6IGNhbiB0aGlzIGV2ZXIgYmUgaW4gdGhlIGFycmF5P1xuICAgICAgICAvLyAtIGNvbXBhcmUgdGhlIGFjdHVhbCBkYXRlIGluc3RlYWQ/XG4gICAgICAgIGlmICghaW5jbHVkZXMocG9zbGlzdCwgcmVzKSlcbiAgICAgICAgICAgIHBvc2xpc3QucHVzaChyZXMpO1xuICAgIH1cbiAgICBkYXRldXRpbC5zb3J0KHBvc2xpc3QpO1xuICAgIHJldHVybiBwb3NsaXN0O1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9cG9zbGlzdC5qcy5tYXAiLCJleHBvcnQgZnVuY3Rpb24gZWFzdGVyKHksIG9mZnNldCkge1xuICAgIGlmIChvZmZzZXQgPT09IHZvaWQgMCkgeyBvZmZzZXQgPSAwOyB9XG4gICAgdmFyIGEgPSB5ICUgMTk7XG4gICAgdmFyIGIgPSBNYXRoLmZsb29yKHkgLyAxMDApO1xuICAgIHZhciBjID0geSAlIDEwMDtcbiAgICB2YXIgZCA9IE1hdGguZmxvb3IoYiAvIDQpO1xuICAgIHZhciBlID0gYiAlIDQ7XG4gICAgdmFyIGYgPSBNYXRoLmZsb29yKChiICsgOCkgLyAyNSk7XG4gICAgdmFyIGcgPSBNYXRoLmZsb29yKChiIC0gZiArIDEpIC8gMyk7XG4gICAgdmFyIGggPSBNYXRoLmZsb29yKDE5ICogYSArIGIgLSBkIC0gZyArIDE1KSAlIDMwO1xuICAgIHZhciBpID0gTWF0aC5mbG9vcihjIC8gNCk7XG4gICAgdmFyIGsgPSBjICUgNDtcbiAgICB2YXIgbCA9IE1hdGguZmxvb3IoMzIgKyAyICogZSArIDIgKiBpIC0gaCAtIGspICUgNztcbiAgICB2YXIgbSA9IE1hdGguZmxvb3IoKGEgKyAxMSAqIGggKyAyMiAqIGwpIC8gNDUxKTtcbiAgICB2YXIgbW9udGggPSBNYXRoLmZsb29yKChoICsgbCAtIDcgKiBtICsgMTE0KSAvIDMxKTtcbiAgICB2YXIgZGF5ID0gKChoICsgbCAtIDcgKiBtICsgMTE0KSAlIDMxKSArIDE7XG4gICAgdmFyIGRhdGUgPSBEYXRlLlVUQyh5LCBtb250aCAtIDEsIGRheSArIG9mZnNldCk7XG4gICAgdmFyIHllYXJTdGFydCA9IERhdGUuVVRDKHksIDAsIDEpO1xuICAgIHJldHVybiBbTWF0aC5jZWlsKChkYXRlIC0geWVhclN0YXJ0KSAvICgxMDAwICogNjAgKiA2MCAqIDI0KSldO1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9ZWFzdGVyLmpzLm1hcCIsImltcG9ydCBkYXRldXRpbCBmcm9tICcuLi9kYXRldXRpbCc7XG5pbXBvcnQgeyBub3RFbXB0eSwgcmVwZWF0LCByYW5nZSwgaXNQcmVzZW50IH0gZnJvbSAnLi4vaGVscGVycyc7XG5pbXBvcnQgeyBGcmVxdWVuY3kgfSBmcm9tICcuLi90eXBlcyc7XG5pbXBvcnQgeyByZWJ1aWxkWWVhciB9IGZyb20gJy4veWVhcmluZm8nO1xuaW1wb3J0IHsgcmVidWlsZE1vbnRoIH0gZnJvbSAnLi9tb250aGluZm8nO1xuaW1wb3J0IHsgZWFzdGVyIH0gZnJvbSAnLi9lYXN0ZXInO1xuaW1wb3J0IHsgVGltZSB9IGZyb20gJy4uL2RhdGV0aW1lJztcbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG4vLyBJdGVyaW5mb1xuLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbnZhciBJdGVyaW5mbyA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICAvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgbm8tZW1wdHktZnVuY3Rpb25cbiAgICBmdW5jdGlvbiBJdGVyaW5mbyhvcHRpb25zKSB7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9IG9wdGlvbnM7XG4gICAgfVxuICAgIEl0ZXJpbmZvLnByb3RvdHlwZS5yZWJ1aWxkID0gZnVuY3Rpb24gKHllYXIsIG1vbnRoKSB7XG4gICAgICAgIHZhciBvcHRpb25zID0gdGhpcy5vcHRpb25zO1xuICAgICAgICBpZiAoeWVhciAhPT0gdGhpcy5sYXN0eWVhcikge1xuICAgICAgICAgICAgdGhpcy55ZWFyaW5mbyA9IHJlYnVpbGRZZWFyKHllYXIsIG9wdGlvbnMpO1xuICAgICAgICB9XG4gICAgICAgIGlmIChub3RFbXB0eShvcHRpb25zLmJ5bndlZWtkYXkpICYmXG4gICAgICAgICAgICAobW9udGggIT09IHRoaXMubGFzdG1vbnRoIHx8IHllYXIgIT09IHRoaXMubGFzdHllYXIpKSB7XG4gICAgICAgICAgICB2YXIgX2EgPSB0aGlzLnllYXJpbmZvLCB5ZWFybGVuID0gX2EueWVhcmxlbiwgbXJhbmdlID0gX2EubXJhbmdlLCB3ZGF5bWFzayA9IF9hLndkYXltYXNrO1xuICAgICAgICAgICAgdGhpcy5tb250aGluZm8gPSByZWJ1aWxkTW9udGgoeWVhciwgbW9udGgsIHllYXJsZW4sIG1yYW5nZSwgd2RheW1hc2ssIG9wdGlvbnMpO1xuICAgICAgICB9XG4gICAgICAgIGlmIChpc1ByZXNlbnQob3B0aW9ucy5ieWVhc3RlcikpIHtcbiAgICAgICAgICAgIHRoaXMuZWFzdGVybWFzayA9IGVhc3Rlcih5ZWFyLCBvcHRpb25zLmJ5ZWFzdGVyKTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KEl0ZXJpbmZvLnByb3RvdHlwZSwgXCJsYXN0eWVhclwiLCB7XG4gICAgICAgIGdldDogZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMubW9udGhpbmZvID8gdGhpcy5tb250aGluZm8ubGFzdHllYXIgOiBudWxsO1xuICAgICAgICB9LFxuICAgICAgICBlbnVtZXJhYmxlOiBmYWxzZSxcbiAgICAgICAgY29uZmlndXJhYmxlOiB0cnVlXG4gICAgfSk7XG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KEl0ZXJpbmZvLnByb3RvdHlwZSwgXCJsYXN0bW9udGhcIiwge1xuICAgICAgICBnZXQ6IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLm1vbnRoaW5mbyA/IHRoaXMubW9udGhpbmZvLmxhc3Rtb250aCA6IG51bGw7XG4gICAgICAgIH0sXG4gICAgICAgIGVudW1lcmFibGU6IGZhbHNlLFxuICAgICAgICBjb25maWd1cmFibGU6IHRydWVcbiAgICB9KTtcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkoSXRlcmluZm8ucHJvdG90eXBlLCBcInllYXJsZW5cIiwge1xuICAgICAgICBnZXQ6IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnllYXJpbmZvLnllYXJsZW47XG4gICAgICAgIH0sXG4gICAgICAgIGVudW1lcmFibGU6IGZhbHNlLFxuICAgICAgICBjb25maWd1cmFibGU6IHRydWVcbiAgICB9KTtcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkoSXRlcmluZm8ucHJvdG90eXBlLCBcInllYXJvcmRpbmFsXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby55ZWFyb3JkaW5hbDtcbiAgICAgICAgfSxcbiAgICAgICAgZW51bWVyYWJsZTogZmFsc2UsXG4gICAgICAgIGNvbmZpZ3VyYWJsZTogdHJ1ZVxuICAgIH0pO1xuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShJdGVyaW5mby5wcm90b3R5cGUsIFwibXJhbmdlXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby5tcmFuZ2U7XG4gICAgICAgIH0sXG4gICAgICAgIGVudW1lcmFibGU6IGZhbHNlLFxuICAgICAgICBjb25maWd1cmFibGU6IHRydWVcbiAgICB9KTtcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkoSXRlcmluZm8ucHJvdG90eXBlLCBcIndkYXltYXNrXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby53ZGF5bWFzaztcbiAgICAgICAgfSxcbiAgICAgICAgZW51bWVyYWJsZTogZmFsc2UsXG4gICAgICAgIGNvbmZpZ3VyYWJsZTogdHJ1ZVxuICAgIH0pO1xuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShJdGVyaW5mby5wcm90b3R5cGUsIFwibW1hc2tcIiwge1xuICAgICAgICBnZXQ6IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLnllYXJpbmZvLm1tYXNrO1xuICAgICAgICB9LFxuICAgICAgICBlbnVtZXJhYmxlOiBmYWxzZSxcbiAgICAgICAgY29uZmlndXJhYmxlOiB0cnVlXG4gICAgfSk7XG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KEl0ZXJpbmZvLnByb3RvdHlwZSwgXCJ3bm9tYXNrXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby53bm9tYXNrO1xuICAgICAgICB9LFxuICAgICAgICBlbnVtZXJhYmxlOiBmYWxzZSxcbiAgICAgICAgY29uZmlndXJhYmxlOiB0cnVlXG4gICAgfSk7XG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KEl0ZXJpbmZvLnByb3RvdHlwZSwgXCJud2RheW1hc2tcIiwge1xuICAgICAgICBnZXQ6IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLm1vbnRoaW5mbyA/IHRoaXMubW9udGhpbmZvLm53ZGF5bWFzayA6IFtdO1xuICAgICAgICB9LFxuICAgICAgICBlbnVtZXJhYmxlOiBmYWxzZSxcbiAgICAgICAgY29uZmlndXJhYmxlOiB0cnVlXG4gICAgfSk7XG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KEl0ZXJpbmZvLnByb3RvdHlwZSwgXCJuZXh0eWVhcmxlblwiLCB7XG4gICAgICAgIGdldDogZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgcmV0dXJuIHRoaXMueWVhcmluZm8ubmV4dHllYXJsZW47XG4gICAgICAgIH0sXG4gICAgICAgIGVudW1lcmFibGU6IGZhbHNlLFxuICAgICAgICBjb25maWd1cmFibGU6IHRydWVcbiAgICB9KTtcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkoSXRlcmluZm8ucHJvdG90eXBlLCBcIm1kYXltYXNrXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby5tZGF5bWFzaztcbiAgICAgICAgfSxcbiAgICAgICAgZW51bWVyYWJsZTogZmFsc2UsXG4gICAgICAgIGNvbmZpZ3VyYWJsZTogdHJ1ZVxuICAgIH0pO1xuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShJdGVyaW5mby5wcm90b3R5cGUsIFwibm1kYXltYXNrXCIsIHtcbiAgICAgICAgZ2V0OiBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICByZXR1cm4gdGhpcy55ZWFyaW5mby5ubWRheW1hc2s7XG4gICAgICAgIH0sXG4gICAgICAgIGVudW1lcmFibGU6IGZhbHNlLFxuICAgICAgICBjb25maWd1cmFibGU6IHRydWVcbiAgICB9KTtcbiAgICBJdGVyaW5mby5wcm90b3R5cGUueWRheXNldCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIFtyYW5nZSh0aGlzLnllYXJsZW4pLCAwLCB0aGlzLnllYXJsZW5dO1xuICAgIH07XG4gICAgSXRlcmluZm8ucHJvdG90eXBlLm1kYXlzZXQgPSBmdW5jdGlvbiAoXywgbW9udGgpIHtcbiAgICAgICAgdmFyIHN0YXJ0ID0gdGhpcy5tcmFuZ2VbbW9udGggLSAxXTtcbiAgICAgICAgdmFyIGVuZCA9IHRoaXMubXJhbmdlW21vbnRoXTtcbiAgICAgICAgdmFyIHNldCA9IHJlcGVhdChudWxsLCB0aGlzLnllYXJsZW4pO1xuICAgICAgICBmb3IgKHZhciBpID0gc3RhcnQ7IGkgPCBlbmQ7IGkrKylcbiAgICAgICAgICAgIHNldFtpXSA9IGk7XG4gICAgICAgIHJldHVybiBbc2V0LCBzdGFydCwgZW5kXTtcbiAgICB9O1xuICAgIEl0ZXJpbmZvLnByb3RvdHlwZS53ZGF5c2V0ID0gZnVuY3Rpb24gKHllYXIsIG1vbnRoLCBkYXkpIHtcbiAgICAgICAgLy8gV2UgbmVlZCB0byBoYW5kbGUgY3Jvc3MteWVhciB3ZWVrcyBoZXJlLlxuICAgICAgICB2YXIgc2V0ID0gcmVwZWF0KG51bGwsIHRoaXMueWVhcmxlbiArIDcpO1xuICAgICAgICB2YXIgaSA9IGRhdGV1dGlsLnRvT3JkaW5hbChuZXcgRGF0ZShEYXRlLlVUQyh5ZWFyLCBtb250aCAtIDEsIGRheSkpKSAtXG4gICAgICAgICAgICB0aGlzLnllYXJvcmRpbmFsO1xuICAgICAgICB2YXIgc3RhcnQgPSBpO1xuICAgICAgICBmb3IgKHZhciBqID0gMDsgaiA8IDc7IGorKykge1xuICAgICAgICAgICAgc2V0W2ldID0gaTtcbiAgICAgICAgICAgICsraTtcbiAgICAgICAgICAgIGlmICh0aGlzLndkYXltYXNrW2ldID09PSB0aGlzLm9wdGlvbnMud2tzdClcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gW3NldCwgc3RhcnQsIGldO1xuICAgIH07XG4gICAgSXRlcmluZm8ucHJvdG90eXBlLmRkYXlzZXQgPSBmdW5jdGlvbiAoeWVhciwgbW9udGgsIGRheSkge1xuICAgICAgICB2YXIgc2V0ID0gcmVwZWF0KG51bGwsIHRoaXMueWVhcmxlbik7XG4gICAgICAgIHZhciBpID0gZGF0ZXV0aWwudG9PcmRpbmFsKG5ldyBEYXRlKERhdGUuVVRDKHllYXIsIG1vbnRoIC0gMSwgZGF5KSkpIC1cbiAgICAgICAgICAgIHRoaXMueWVhcm9yZGluYWw7XG4gICAgICAgIHNldFtpXSA9IGk7XG4gICAgICAgIHJldHVybiBbc2V0LCBpLCBpICsgMV07XG4gICAgfTtcbiAgICBJdGVyaW5mby5wcm90b3R5cGUuaHRpbWVzZXQgPSBmdW5jdGlvbiAoaG91ciwgXywgc2Vjb25kLCBtaWxsaXNlY29uZCkge1xuICAgICAgICB2YXIgX3RoaXMgPSB0aGlzO1xuICAgICAgICB2YXIgc2V0ID0gW107XG4gICAgICAgIHRoaXMub3B0aW9ucy5ieW1pbnV0ZS5mb3JFYWNoKGZ1bmN0aW9uIChtaW51dGUpIHtcbiAgICAgICAgICAgIHNldCA9IHNldC5jb25jYXQoX3RoaXMubXRpbWVzZXQoaG91ciwgbWludXRlLCBzZWNvbmQsIG1pbGxpc2Vjb25kKSk7XG4gICAgICAgIH0pO1xuICAgICAgICBkYXRldXRpbC5zb3J0KHNldCk7XG4gICAgICAgIHJldHVybiBzZXQ7XG4gICAgfTtcbiAgICBJdGVyaW5mby5wcm90b3R5cGUubXRpbWVzZXQgPSBmdW5jdGlvbiAoaG91ciwgbWludXRlLCBfLCBtaWxsaXNlY29uZCkge1xuICAgICAgICB2YXIgc2V0ID0gdGhpcy5vcHRpb25zLmJ5c2Vjb25kLm1hcChmdW5jdGlvbiAoc2Vjb25kKSB7IHJldHVybiBuZXcgVGltZShob3VyLCBtaW51dGUsIHNlY29uZCwgbWlsbGlzZWNvbmQpOyB9KTtcbiAgICAgICAgZGF0ZXV0aWwuc29ydChzZXQpO1xuICAgICAgICByZXR1cm4gc2V0O1xuICAgIH07XG4gICAgSXRlcmluZm8ucHJvdG90eXBlLnN0aW1lc2V0ID0gZnVuY3Rpb24gKGhvdXIsIG1pbnV0ZSwgc2Vjb25kLCBtaWxsaXNlY29uZCkge1xuICAgICAgICByZXR1cm4gW25ldyBUaW1lKGhvdXIsIG1pbnV0ZSwgc2Vjb25kLCBtaWxsaXNlY29uZCldO1xuICAgIH07XG4gICAgSXRlcmluZm8ucHJvdG90eXBlLmdldGRheXNldCA9IGZ1bmN0aW9uIChmcmVxKSB7XG4gICAgICAgIHN3aXRjaCAoZnJlcSkge1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuWUVBUkxZOlxuICAgICAgICAgICAgICAgIHJldHVybiB0aGlzLnlkYXlzZXQuYmluZCh0aGlzKTtcbiAgICAgICAgICAgIGNhc2UgRnJlcXVlbmN5Lk1PTlRITFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMubWRheXNldC5iaW5kKHRoaXMpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuV0VFS0xZOlxuICAgICAgICAgICAgICAgIHJldHVybiB0aGlzLndkYXlzZXQuYmluZCh0aGlzKTtcbiAgICAgICAgICAgIGNhc2UgRnJlcXVlbmN5LkRBSUxZOlxuICAgICAgICAgICAgICAgIHJldHVybiB0aGlzLmRkYXlzZXQuYmluZCh0aGlzKTtcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuZGRheXNldC5iaW5kKHRoaXMpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBJdGVyaW5mby5wcm90b3R5cGUuZ2V0dGltZXNldCA9IGZ1bmN0aW9uIChmcmVxKSB7XG4gICAgICAgIHN3aXRjaCAoZnJlcSkge1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuSE9VUkxZOlxuICAgICAgICAgICAgICAgIHJldHVybiB0aGlzLmh0aW1lc2V0LmJpbmQodGhpcyk7XG4gICAgICAgICAgICBjYXNlIEZyZXF1ZW5jeS5NSU5VVEVMWTpcbiAgICAgICAgICAgICAgICByZXR1cm4gdGhpcy5tdGltZXNldC5iaW5kKHRoaXMpO1xuICAgICAgICAgICAgY2FzZSBGcmVxdWVuY3kuU0VDT05ETFk6XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRoaXMuc3RpbWVzZXQuYmluZCh0aGlzKTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgcmV0dXJuIEl0ZXJpbmZvO1xufSgpKTtcbmV4cG9ydCBkZWZhdWx0IEl0ZXJpbmZvO1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9aW5kZXguanMubWFwIiwiaW1wb3J0IHsgUlJ1bGUgfSBmcm9tICcuLi9ycnVsZSc7XG5pbXBvcnQgeyBlbXB0eSwgcmVwZWF0LCBweW1vZCB9IGZyb20gJy4uL2hlbHBlcnMnO1xuZXhwb3J0IGZ1bmN0aW9uIHJlYnVpbGRNb250aCh5ZWFyLCBtb250aCwgeWVhcmxlbiwgbXJhbmdlLCB3ZGF5bWFzaywgb3B0aW9ucykge1xuICAgIHZhciByZXN1bHQgPSB7XG4gICAgICAgIGxhc3R5ZWFyOiB5ZWFyLFxuICAgICAgICBsYXN0bW9udGg6IG1vbnRoLFxuICAgICAgICBud2RheW1hc2s6IFtdLFxuICAgIH07XG4gICAgdmFyIHJhbmdlcyA9IFtdO1xuICAgIGlmIChvcHRpb25zLmZyZXEgPT09IFJSdWxlLllFQVJMWSkge1xuICAgICAgICBpZiAoZW1wdHkob3B0aW9ucy5ieW1vbnRoKSkge1xuICAgICAgICAgICAgcmFuZ2VzID0gW1swLCB5ZWFybGVuXV07XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBmb3IgKHZhciBqID0gMDsgaiA8IG9wdGlvbnMuYnltb250aC5sZW5ndGg7IGorKykge1xuICAgICAgICAgICAgICAgIG1vbnRoID0gb3B0aW9ucy5ieW1vbnRoW2pdO1xuICAgICAgICAgICAgICAgIHJhbmdlcy5wdXNoKG1yYW5nZS5zbGljZShtb250aCAtIDEsIG1vbnRoICsgMSkpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfVxuICAgIGVsc2UgaWYgKG9wdGlvbnMuZnJlcSA9PT0gUlJ1bGUuTU9OVEhMWSkge1xuICAgICAgICByYW5nZXMgPSBbbXJhbmdlLnNsaWNlKG1vbnRoIC0gMSwgbW9udGggKyAxKV07XG4gICAgfVxuICAgIGlmIChlbXB0eShyYW5nZXMpKSB7XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIC8vIFdlZWtseSBmcmVxdWVuY3kgd29uJ3QgZ2V0IGhlcmUsIHNvIHdlIG1heSBub3RcbiAgICAvLyBjYXJlIGFib3V0IGNyb3NzLXllYXIgd2Vla2x5IHBlcmlvZHMuXG4gICAgcmVzdWx0Lm53ZGF5bWFzayA9IHJlcGVhdCgwLCB5ZWFybGVuKTtcbiAgICBmb3IgKHZhciBqID0gMDsgaiA8IHJhbmdlcy5sZW5ndGg7IGorKykge1xuICAgICAgICB2YXIgcmFuZyA9IHJhbmdlc1tqXTtcbiAgICAgICAgdmFyIGZpcnN0ID0gcmFuZ1swXTtcbiAgICAgICAgdmFyIGxhc3QgPSByYW5nWzFdIC0gMTtcbiAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBvcHRpb25zLmJ5bndlZWtkYXkubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgIHZhciBpID0gdm9pZCAwO1xuICAgICAgICAgICAgdmFyIF9hID0gb3B0aW9ucy5ieW53ZWVrZGF5W2tdLCB3ZGF5ID0gX2FbMF0sIG4gPSBfYVsxXTtcbiAgICAgICAgICAgIGlmIChuIDwgMCkge1xuICAgICAgICAgICAgICAgIGkgPSBsYXN0ICsgKG4gKyAxKSAqIDc7XG4gICAgICAgICAgICAgICAgaSAtPSBweW1vZCh3ZGF5bWFza1tpXSAtIHdkYXksIDcpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgaSA9IGZpcnN0ICsgKG4gLSAxKSAqIDc7XG4gICAgICAgICAgICAgICAgaSArPSBweW1vZCg3IC0gd2RheW1hc2tbaV0gKyB3ZGF5LCA3KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChmaXJzdCA8PSBpICYmIGkgPD0gbGFzdClcbiAgICAgICAgICAgICAgICByZXN1bHQubndkYXltYXNrW2ldID0gMTtcbiAgICAgICAgfVxuICAgIH1cbiAgICByZXR1cm4gcmVzdWx0O1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9bW9udGhpbmZvLmpzLm1hcCIsImltcG9ydCB7IF9fYXNzaWduIH0gZnJvbSBcInRzbGliXCI7XG5pbXBvcnQgZGF0ZXV0aWwgZnJvbSAnLi4vZGF0ZXV0aWwnO1xuaW1wb3J0IHsgZW1wdHksIHJlcGVhdCwgcHltb2QsIGluY2x1ZGVzIH0gZnJvbSAnLi4vaGVscGVycyc7XG5pbXBvcnQgeyBNMzY1TUFTSywgTURBWTM2NU1BU0ssIE5NREFZMzY1TUFTSywgV0RBWU1BU0ssIE0zNjVSQU5HRSwgTTM2Nk1BU0ssIE1EQVkzNjZNQVNLLCBOTURBWTM2Nk1BU0ssIE0zNjZSQU5HRSwgfSBmcm9tICcuLi9tYXNrcyc7XG5leHBvcnQgZnVuY3Rpb24gcmVidWlsZFllYXIoeWVhciwgb3B0aW9ucykge1xuICAgIHZhciBmaXJzdHlkYXkgPSBuZXcgRGF0ZShEYXRlLlVUQyh5ZWFyLCAwLCAxKSk7XG4gICAgdmFyIHllYXJsZW4gPSBkYXRldXRpbC5pc0xlYXBZZWFyKHllYXIpID8gMzY2IDogMzY1O1xuICAgIHZhciBuZXh0eWVhcmxlbiA9IGRhdGV1dGlsLmlzTGVhcFllYXIoeWVhciArIDEpID8gMzY2IDogMzY1O1xuICAgIHZhciB5ZWFyb3JkaW5hbCA9IGRhdGV1dGlsLnRvT3JkaW5hbChmaXJzdHlkYXkpO1xuICAgIHZhciB5ZWFyd2Vla2RheSA9IGRhdGV1dGlsLmdldFdlZWtkYXkoZmlyc3R5ZGF5KTtcbiAgICB2YXIgcmVzdWx0ID0gX19hc3NpZ24oX19hc3NpZ24oeyB5ZWFybGVuOiB5ZWFybGVuLCBuZXh0eWVhcmxlbjogbmV4dHllYXJsZW4sIHllYXJvcmRpbmFsOiB5ZWFyb3JkaW5hbCwgeWVhcndlZWtkYXk6IHllYXJ3ZWVrZGF5IH0sIGJhc2VZZWFyTWFza3MoeWVhcikpLCB7IHdub21hc2s6IG51bGwgfSk7XG4gICAgaWYgKGVtcHR5KG9wdGlvbnMuYnl3ZWVrbm8pKSB7XG4gICAgICAgIHJldHVybiByZXN1bHQ7XG4gICAgfVxuICAgIHJlc3VsdC53bm9tYXNrID0gcmVwZWF0KDAsIHllYXJsZW4gKyA3KTtcbiAgICB2YXIgZmlyc3R3a3N0O1xuICAgIHZhciB3eWVhcmxlbjtcbiAgICB2YXIgbm8xd2tzdCA9IChmaXJzdHdrc3QgPSBweW1vZCg3IC0geWVhcndlZWtkYXkgKyBvcHRpb25zLndrc3QsIDcpKTtcbiAgICBpZiAobm8xd2tzdCA+PSA0KSB7XG4gICAgICAgIG5vMXdrc3QgPSAwO1xuICAgICAgICAvLyBOdW1iZXIgb2YgZGF5cyBpbiB0aGUgeWVhciwgcGx1cyB0aGUgZGF5cyB3ZSBnb3RcbiAgICAgICAgLy8gZnJvbSBsYXN0IHllYXIuXG4gICAgICAgIHd5ZWFybGVuID0gcmVzdWx0LnllYXJsZW4gKyBweW1vZCh5ZWFyd2Vla2RheSAtIG9wdGlvbnMud2tzdCwgNyk7XG4gICAgfVxuICAgIGVsc2Uge1xuICAgICAgICAvLyBOdW1iZXIgb2YgZGF5cyBpbiB0aGUgeWVhciwgbWludXMgdGhlIGRheXMgd2VcbiAgICAgICAgLy8gbGVmdCBpbiBsYXN0IHllYXIuXG4gICAgICAgIHd5ZWFybGVuID0geWVhcmxlbiAtIG5vMXdrc3Q7XG4gICAgfVxuICAgIHZhciBkaXYgPSBNYXRoLmZsb29yKHd5ZWFybGVuIC8gNyk7XG4gICAgdmFyIG1vZCA9IHB5bW9kKHd5ZWFybGVuLCA3KTtcbiAgICB2YXIgbnVtd2Vla3MgPSBNYXRoLmZsb29yKGRpdiArIG1vZCAvIDQpO1xuICAgIGZvciAodmFyIGogPSAwOyBqIDwgb3B0aW9ucy5ieXdlZWtuby5sZW5ndGg7IGorKykge1xuICAgICAgICB2YXIgbiA9IG9wdGlvbnMuYnl3ZWVrbm9bal07XG4gICAgICAgIGlmIChuIDwgMCkge1xuICAgICAgICAgICAgbiArPSBudW13ZWVrcyArIDE7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKCEobiA+IDAgJiYgbiA8PSBudW13ZWVrcykpIHtcbiAgICAgICAgICAgIGNvbnRpbnVlO1xuICAgICAgICB9XG4gICAgICAgIHZhciBpID0gdm9pZCAwO1xuICAgICAgICBpZiAobiA+IDEpIHtcbiAgICAgICAgICAgIGkgPSBubzF3a3N0ICsgKG4gLSAxKSAqIDc7XG4gICAgICAgICAgICBpZiAobm8xd2tzdCAhPT0gZmlyc3R3a3N0KSB7XG4gICAgICAgICAgICAgICAgaSAtPSA3IC0gZmlyc3R3a3N0O1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaSA9IG5vMXdrc3Q7XG4gICAgICAgIH1cbiAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCA3OyBrKyspIHtcbiAgICAgICAgICAgIHJlc3VsdC53bm9tYXNrW2ldID0gMTtcbiAgICAgICAgICAgIGkrKztcbiAgICAgICAgICAgIGlmIChyZXN1bHQud2RheW1hc2tbaV0gPT09IG9wdGlvbnMud2tzdClcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgfVxuICAgIH1cbiAgICBpZiAoaW5jbHVkZXMob3B0aW9ucy5ieXdlZWtubywgMSkpIHtcbiAgICAgICAgLy8gQ2hlY2sgd2VlayBudW1iZXIgMSBvZiBuZXh0IHllYXIgYXMgd2VsbFxuICAgICAgICAvLyBvcmlnLVRPRE8gOiBDaGVjayAtbnVtd2Vla3MgZm9yIG5leHQgeWVhci5cbiAgICAgICAgdmFyIGkgPSBubzF3a3N0ICsgbnVtd2Vla3MgKiA3O1xuICAgICAgICBpZiAobm8xd2tzdCAhPT0gZmlyc3R3a3N0KVxuICAgICAgICAgICAgaSAtPSA3IC0gZmlyc3R3a3N0O1xuICAgICAgICBpZiAoaSA8IHllYXJsZW4pIHtcbiAgICAgICAgICAgIC8vIElmIHdlZWsgc3RhcnRzIGluIG5leHQgeWVhciwgd2VcbiAgICAgICAgICAgIC8vIGRvbid0IGNhcmUgYWJvdXQgaXQuXG4gICAgICAgICAgICBmb3IgKHZhciBqID0gMDsgaiA8IDc7IGorKykge1xuICAgICAgICAgICAgICAgIHJlc3VsdC53bm9tYXNrW2ldID0gMTtcbiAgICAgICAgICAgICAgICBpICs9IDE7XG4gICAgICAgICAgICAgICAgaWYgKHJlc3VsdC53ZGF5bWFza1tpXSA9PT0gb3B0aW9ucy53a3N0KVxuICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH1cbiAgICBpZiAobm8xd2tzdCkge1xuICAgICAgICAvLyBDaGVjayBsYXN0IHdlZWsgbnVtYmVyIG9mIGxhc3QgeWVhciBhc1xuICAgICAgICAvLyB3ZWxsLiBJZiBubzF3a3N0IGlzIDAsIGVpdGhlciB0aGUgeWVhclxuICAgICAgICAvLyBzdGFydGVkIG9uIHdlZWsgc3RhcnQsIG9yIHdlZWsgbnVtYmVyIDFcbiAgICAgICAgLy8gZ290IGRheXMgZnJvbSBsYXN0IHllYXIsIHNvIHRoZXJlIGFyZSBub1xuICAgICAgICAvLyBkYXlzIGZyb20gbGFzdCB5ZWFyJ3MgbGFzdCB3ZWVrIG51bWJlciBpblxuICAgICAgICAvLyB0aGlzIHllYXIuXG4gICAgICAgIHZhciBsbnVtd2Vla3MgPSB2b2lkIDA7XG4gICAgICAgIGlmICghaW5jbHVkZXMob3B0aW9ucy5ieXdlZWtubywgLTEpKSB7XG4gICAgICAgICAgICB2YXIgbHllYXJ3ZWVrZGF5ID0gZGF0ZXV0aWwuZ2V0V2Vla2RheShuZXcgRGF0ZShEYXRlLlVUQyh5ZWFyIC0gMSwgMCwgMSkpKTtcbiAgICAgICAgICAgIHZhciBsbm8xd2tzdCA9IHB5bW9kKDcgLSBseWVhcndlZWtkYXkudmFsdWVPZigpICsgb3B0aW9ucy53a3N0LCA3KTtcbiAgICAgICAgICAgIHZhciBseWVhcmxlbiA9IGRhdGV1dGlsLmlzTGVhcFllYXIoeWVhciAtIDEpID8gMzY2IDogMzY1O1xuICAgICAgICAgICAgdmFyIHdlZWtzdCA9IHZvaWQgMDtcbiAgICAgICAgICAgIGlmIChsbm8xd2tzdCA+PSA0KSB7XG4gICAgICAgICAgICAgICAgbG5vMXdrc3QgPSAwO1xuICAgICAgICAgICAgICAgIHdlZWtzdCA9IGx5ZWFybGVuICsgcHltb2QobHllYXJ3ZWVrZGF5IC0gb3B0aW9ucy53a3N0LCA3KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIHdlZWtzdCA9IHllYXJsZW4gLSBubzF3a3N0O1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgbG51bXdlZWtzID0gTWF0aC5mbG9vcig1MiArIHB5bW9kKHdlZWtzdCwgNykgLyA0KTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGxudW13ZWVrcyA9IC0xO1xuICAgICAgICB9XG4gICAgICAgIGlmIChpbmNsdWRlcyhvcHRpb25zLmJ5d2Vla25vLCBsbnVtd2Vla3MpKSB7XG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IG5vMXdrc3Q7IGkrKylcbiAgICAgICAgICAgICAgICByZXN1bHQud25vbWFza1tpXSA9IDE7XG4gICAgICAgIH1cbiAgICB9XG4gICAgcmV0dXJuIHJlc3VsdDtcbn1cbmZ1bmN0aW9uIGJhc2VZZWFyTWFza3MoeWVhcikge1xuICAgIHZhciB5ZWFybGVuID0gZGF0ZXV0aWwuaXNMZWFwWWVhcih5ZWFyKSA/IDM2NiA6IDM2NTtcbiAgICB2YXIgZmlyc3R5ZGF5ID0gbmV3IERhdGUoRGF0ZS5VVEMoeWVhciwgMCwgMSkpO1xuICAgIHZhciB3ZGF5ID0gZGF0ZXV0aWwuZ2V0V2Vla2RheShmaXJzdHlkYXkpO1xuICAgIGlmICh5ZWFybGVuID09PSAzNjUpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgIG1tYXNrOiBNMzY1TUFTSyxcbiAgICAgICAgICAgIG1kYXltYXNrOiBNREFZMzY1TUFTSyxcbiAgICAgICAgICAgIG5tZGF5bWFzazogTk1EQVkzNjVNQVNLLFxuICAgICAgICAgICAgd2RheW1hc2s6IFdEQVlNQVNLLnNsaWNlKHdkYXkpLFxuICAgICAgICAgICAgbXJhbmdlOiBNMzY1UkFOR0UsXG4gICAgICAgIH07XG4gICAgfVxuICAgIHJldHVybiB7XG4gICAgICAgIG1tYXNrOiBNMzY2TUFTSyxcbiAgICAgICAgbWRheW1hc2s6IE1EQVkzNjZNQVNLLFxuICAgICAgICBubWRheW1hc2s6IE5NREFZMzY2TUFTSyxcbiAgICAgICAgd2RheW1hc2s6IFdEQVlNQVNLLnNsaWNlKHdkYXkpLFxuICAgICAgICBtcmFuZ2U6IE0zNjZSQU5HRSxcbiAgICB9O1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9eWVhcmluZm8uanMubWFwIiwiLyoqXG4gKiBUaGlzIGNsYXNzIGhlbHBzIHVzIHRvIGVtdWxhdGUgcHl0aG9uJ3MgZ2VuZXJhdG9ycywgc29ydGEuXG4gKi9cbnZhciBJdGVyUmVzdWx0ID0gLyoqIEBjbGFzcyAqLyAoZnVuY3Rpb24gKCkge1xuICAgIGZ1bmN0aW9uIEl0ZXJSZXN1bHQobWV0aG9kLCBhcmdzKSB7XG4gICAgICAgIHRoaXMubWluRGF0ZSA9IG51bGw7XG4gICAgICAgIHRoaXMubWF4RGF0ZSA9IG51bGw7XG4gICAgICAgIHRoaXMuX3Jlc3VsdCA9IFtdO1xuICAgICAgICB0aGlzLnRvdGFsID0gMDtcbiAgICAgICAgdGhpcy5tZXRob2QgPSBtZXRob2Q7XG4gICAgICAgIHRoaXMuYXJncyA9IGFyZ3M7XG4gICAgICAgIGlmIChtZXRob2QgPT09ICdiZXR3ZWVuJykge1xuICAgICAgICAgICAgdGhpcy5tYXhEYXRlID0gYXJncy5pbmNcbiAgICAgICAgICAgICAgICA/IGFyZ3MuYmVmb3JlXG4gICAgICAgICAgICAgICAgOiBuZXcgRGF0ZShhcmdzLmJlZm9yZS5nZXRUaW1lKCkgLSAxKTtcbiAgICAgICAgICAgIHRoaXMubWluRGF0ZSA9IGFyZ3MuaW5jID8gYXJncy5hZnRlciA6IG5ldyBEYXRlKGFyZ3MuYWZ0ZXIuZ2V0VGltZSgpICsgMSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAobWV0aG9kID09PSAnYmVmb3JlJykge1xuICAgICAgICAgICAgdGhpcy5tYXhEYXRlID0gYXJncy5pbmMgPyBhcmdzLmR0IDogbmV3IERhdGUoYXJncy5kdC5nZXRUaW1lKCkgLSAxKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmIChtZXRob2QgPT09ICdhZnRlcicpIHtcbiAgICAgICAgICAgIHRoaXMubWluRGF0ZSA9IGFyZ3MuaW5jID8gYXJncy5kdCA6IG5ldyBEYXRlKGFyZ3MuZHQuZ2V0VGltZSgpICsgMSk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgLyoqXG4gICAgICogUG9zc2libHkgYWRkcyBhIGRhdGUgaW50byB0aGUgcmVzdWx0LlxuICAgICAqXG4gICAgICogQHBhcmFtIHtEYXRlfSBkYXRlIC0gdGhlIGRhdGUgaXNuJ3QgbmVjZXNzYXJseSBhZGRlZCB0byB0aGUgcmVzdWx0XG4gICAgICogbGlzdCAoaWYgaXQgaXMgdG9vIGxhdGUvdG9vIGVhcmx5KVxuICAgICAqIEByZXR1cm4ge0Jvb2xlYW59IHRydWUgaWYgaXQgbWFrZXMgc2Vuc2UgdG8gY29udGludWUgdGhlIGl0ZXJhdGlvblxuICAgICAqIGZhbHNlIGlmIHdlJ3JlIGRvbmUuXG4gICAgICovXG4gICAgSXRlclJlc3VsdC5wcm90b3R5cGUuYWNjZXB0ID0gZnVuY3Rpb24gKGRhdGUpIHtcbiAgICAgICAgKyt0aGlzLnRvdGFsO1xuICAgICAgICB2YXIgdG9vRWFybHkgPSB0aGlzLm1pbkRhdGUgJiYgZGF0ZSA8IHRoaXMubWluRGF0ZTtcbiAgICAgICAgdmFyIHRvb0xhdGUgPSB0aGlzLm1heERhdGUgJiYgZGF0ZSA+IHRoaXMubWF4RGF0ZTtcbiAgICAgICAgaWYgKHRoaXMubWV0aG9kID09PSAnYmV0d2VlbicpIHtcbiAgICAgICAgICAgIGlmICh0b29FYXJseSlcbiAgICAgICAgICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICAgICAgICAgIGlmICh0b29MYXRlKVxuICAgICAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0aGlzLm1ldGhvZCA9PT0gJ2JlZm9yZScpIHtcbiAgICAgICAgICAgIGlmICh0b29MYXRlKVxuICAgICAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0aGlzLm1ldGhvZCA9PT0gJ2FmdGVyJykge1xuICAgICAgICAgICAgaWYgKHRvb0Vhcmx5KVxuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgdGhpcy5hZGQoZGF0ZSk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHRoaXMuYWRkKGRhdGUpO1xuICAgIH07XG4gICAgLyoqXG4gICAgICpcbiAgICAgKiBAcGFyYW0ge0RhdGV9IGRhdGUgdGhhdCBpcyBwYXJ0IG9mIHRoZSByZXN1bHQuXG4gICAgICogQHJldHVybiB7Qm9vbGVhbn0gd2hldGhlciB3ZSBhcmUgaW50ZXJlc3RlZCBpbiBtb3JlIHZhbHVlcy5cbiAgICAgKi9cbiAgICBJdGVyUmVzdWx0LnByb3RvdHlwZS5hZGQgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICB0aGlzLl9yZXN1bHQucHVzaChkYXRlKTtcbiAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiAnYmVmb3JlJyBhbmQgJ2FmdGVyJyByZXR1cm4gb25seSBvbmUgZGF0ZSwgd2hlcmVhcyAnYWxsJ1xuICAgICAqIGFuZCAnYmV0d2VlbicgYW4gYXJyYXkuXG4gICAgICpcbiAgICAgKiBAcmV0dXJuIHtEYXRlLEFycmF5P31cbiAgICAgKi9cbiAgICBJdGVyUmVzdWx0LnByb3RvdHlwZS5nZXRWYWx1ZSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIHJlcyA9IHRoaXMuX3Jlc3VsdDtcbiAgICAgICAgc3dpdGNoICh0aGlzLm1ldGhvZCkge1xuICAgICAgICAgICAgY2FzZSAnYWxsJzpcbiAgICAgICAgICAgIGNhc2UgJ2JldHdlZW4nOlxuICAgICAgICAgICAgICAgIHJldHVybiByZXM7XG4gICAgICAgICAgICBjYXNlICdiZWZvcmUnOlxuICAgICAgICAgICAgY2FzZSAnYWZ0ZXInOlxuICAgICAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgICAgICByZXR1cm4gKHJlcy5sZW5ndGggPyByZXNbcmVzLmxlbmd0aCAtIDFdIDogbnVsbCk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIEl0ZXJSZXN1bHQucHJvdG90eXBlLmNsb25lID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gbmV3IEl0ZXJSZXN1bHQodGhpcy5tZXRob2QsIHRoaXMuYXJncyk7XG4gICAgfTtcbiAgICByZXR1cm4gSXRlclJlc3VsdDtcbn0oKSk7XG5leHBvcnQgZGVmYXVsdCBJdGVyUmVzdWx0O1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9aXRlcnJlc3VsdC5qcy5tYXAiLCJpbXBvcnQgeyBEYXRlV2l0aFpvbmUgfSBmcm9tICcuL2RhdGV3aXRoem9uZSc7XG5pbXBvcnQgeyBpdGVyIH0gZnJvbSAnLi9pdGVyJztcbmltcG9ydCBkYXRldXRpbCBmcm9tICcuL2RhdGV1dGlsJztcbmV4cG9ydCBmdW5jdGlvbiBpdGVyU2V0KGl0ZXJSZXN1bHQsIF9ycnVsZSwgX2V4cnVsZSwgX3JkYXRlLCBfZXhkYXRlLCB0emlkKSB7XG4gICAgdmFyIF9leGRhdGVIYXNoID0ge307XG4gICAgdmFyIF9hY2NlcHQgPSBpdGVyUmVzdWx0LmFjY2VwdDtcbiAgICBmdW5jdGlvbiBldmFsRXhkYXRlKGFmdGVyLCBiZWZvcmUpIHtcbiAgICAgICAgX2V4cnVsZS5mb3JFYWNoKGZ1bmN0aW9uIChycnVsZSkge1xuICAgICAgICAgICAgcnJ1bGUuYmV0d2VlbihhZnRlciwgYmVmb3JlLCB0cnVlKS5mb3JFYWNoKGZ1bmN0aW9uIChkYXRlKSB7XG4gICAgICAgICAgICAgICAgX2V4ZGF0ZUhhc2hbTnVtYmVyKGRhdGUpXSA9IHRydWU7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfVxuICAgIF9leGRhdGUuZm9yRWFjaChmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICB2YXIgem9uZWREYXRlID0gbmV3IERhdGVXaXRoWm9uZShkYXRlLCB0emlkKS5yZXpvbmVkRGF0ZSgpO1xuICAgICAgICBfZXhkYXRlSGFzaFtOdW1iZXIoem9uZWREYXRlKV0gPSB0cnVlO1xuICAgIH0pO1xuICAgIGl0ZXJSZXN1bHQuYWNjZXB0ID0gZnVuY3Rpb24gKGRhdGUpIHtcbiAgICAgICAgdmFyIGR0ID0gTnVtYmVyKGRhdGUpO1xuICAgICAgICBpZiAoaXNOYU4oZHQpKVxuICAgICAgICAgICAgcmV0dXJuIF9hY2NlcHQuY2FsbCh0aGlzLCBkYXRlKTtcbiAgICAgICAgaWYgKCFfZXhkYXRlSGFzaFtkdF0pIHtcbiAgICAgICAgICAgIGV2YWxFeGRhdGUobmV3IERhdGUoZHQgLSAxKSwgbmV3IERhdGUoZHQgKyAxKSk7XG4gICAgICAgICAgICBpZiAoIV9leGRhdGVIYXNoW2R0XSkge1xuICAgICAgICAgICAgICAgIF9leGRhdGVIYXNoW2R0XSA9IHRydWU7XG4gICAgICAgICAgICAgICAgcmV0dXJuIF9hY2NlcHQuY2FsbCh0aGlzLCBkYXRlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9O1xuICAgIGlmIChpdGVyUmVzdWx0Lm1ldGhvZCA9PT0gJ2JldHdlZW4nKSB7XG4gICAgICAgIGV2YWxFeGRhdGUoaXRlclJlc3VsdC5hcmdzLmFmdGVyLCBpdGVyUmVzdWx0LmFyZ3MuYmVmb3JlKTtcbiAgICAgICAgaXRlclJlc3VsdC5hY2NlcHQgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICAgICAgdmFyIGR0ID0gTnVtYmVyKGRhdGUpO1xuICAgICAgICAgICAgaWYgKCFfZXhkYXRlSGFzaFtkdF0pIHtcbiAgICAgICAgICAgICAgICBfZXhkYXRlSGFzaFtkdF0gPSB0cnVlO1xuICAgICAgICAgICAgICAgIHJldHVybiBfYWNjZXB0LmNhbGwodGhpcywgZGF0ZSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICAgICAgfTtcbiAgICB9XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBfcmRhdGUubGVuZ3RoOyBpKyspIHtcbiAgICAgICAgdmFyIHpvbmVkRGF0ZSA9IG5ldyBEYXRlV2l0aFpvbmUoX3JkYXRlW2ldLCB0emlkKS5yZXpvbmVkRGF0ZSgpO1xuICAgICAgICBpZiAoIWl0ZXJSZXN1bHQuYWNjZXB0KG5ldyBEYXRlKHpvbmVkRGF0ZS5nZXRUaW1lKCkpKSlcbiAgICAgICAgICAgIGJyZWFrO1xuICAgIH1cbiAgICBfcnJ1bGUuZm9yRWFjaChmdW5jdGlvbiAocnJ1bGUpIHtcbiAgICAgICAgaXRlcihpdGVyUmVzdWx0LCBycnVsZS5vcHRpb25zKTtcbiAgICB9KTtcbiAgICB2YXIgcmVzID0gaXRlclJlc3VsdC5fcmVzdWx0O1xuICAgIGRhdGV1dGlsLnNvcnQocmVzKTtcbiAgICBzd2l0Y2ggKGl0ZXJSZXN1bHQubWV0aG9kKSB7XG4gICAgICAgIGNhc2UgJ2FsbCc6XG4gICAgICAgIGNhc2UgJ2JldHdlZW4nOlxuICAgICAgICAgICAgcmV0dXJuIHJlcztcbiAgICAgICAgY2FzZSAnYmVmb3JlJzpcbiAgICAgICAgICAgIHJldHVybiAoKHJlcy5sZW5ndGggJiYgcmVzW3Jlcy5sZW5ndGggLSAxXSkgfHwgbnVsbCk7XG4gICAgICAgIGNhc2UgJ2FmdGVyJzpcbiAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgIHJldHVybiAoKHJlcy5sZW5ndGggJiYgcmVzWzBdKSB8fCBudWxsKTtcbiAgICB9XG59XG4vLyMgc291cmNlTWFwcGluZ1VSTD1pdGVyc2V0LmpzLm1hcCIsImltcG9ydCB7IF9fc3ByZWFkQXJyYXkgfSBmcm9tIFwidHNsaWJcIjtcbmltcG9ydCB7IHJhbmdlLCByZXBlYXQgfSBmcm9tICcuL2hlbHBlcnMnO1xuLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbi8vIERhdGUgbWFza3Ncbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG4vLyBFdmVyeSBtYXNrIGlzIDcgZGF5cyBsb25nZXIgdG8gaGFuZGxlIGNyb3NzLXllYXIgd2Vla2x5IHBlcmlvZHMuXG52YXIgTTM2NU1BU0sgPSBfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KFtdLCByZXBlYXQoMSwgMzEpLCB0cnVlKSwgcmVwZWF0KDIsIDI4KSwgdHJ1ZSksIHJlcGVhdCgzLCAzMSksIHRydWUpLCByZXBlYXQoNCwgMzApLCB0cnVlKSwgcmVwZWF0KDUsIDMxKSwgdHJ1ZSksIHJlcGVhdCg2LCAzMCksIHRydWUpLCByZXBlYXQoNywgMzEpLCB0cnVlKSwgcmVwZWF0KDgsIDMxKSwgdHJ1ZSksIHJlcGVhdCg5LCAzMCksIHRydWUpLCByZXBlYXQoMTAsIDMxKSwgdHJ1ZSksIHJlcGVhdCgxMSwgMzApLCB0cnVlKSwgcmVwZWF0KDEyLCAzMSksIHRydWUpLCByZXBlYXQoMSwgNyksIHRydWUpO1xudmFyIE0zNjZNQVNLID0gX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShbXSwgcmVwZWF0KDEsIDMxKSwgdHJ1ZSksIHJlcGVhdCgyLCAyOSksIHRydWUpLCByZXBlYXQoMywgMzEpLCB0cnVlKSwgcmVwZWF0KDQsIDMwKSwgdHJ1ZSksIHJlcGVhdCg1LCAzMSksIHRydWUpLCByZXBlYXQoNiwgMzApLCB0cnVlKSwgcmVwZWF0KDcsIDMxKSwgdHJ1ZSksIHJlcGVhdCg4LCAzMSksIHRydWUpLCByZXBlYXQoOSwgMzApLCB0cnVlKSwgcmVwZWF0KDEwLCAzMSksIHRydWUpLCByZXBlYXQoMTEsIDMwKSwgdHJ1ZSksIHJlcGVhdCgxMiwgMzEpLCB0cnVlKSwgcmVwZWF0KDEsIDcpLCB0cnVlKTtcbnZhciBNMjggPSByYW5nZSgxLCAyOSk7XG52YXIgTTI5ID0gcmFuZ2UoMSwgMzApO1xudmFyIE0zMCA9IHJhbmdlKDEsIDMxKTtcbnZhciBNMzEgPSByYW5nZSgxLCAzMik7XG52YXIgTURBWTM2Nk1BU0sgPSBfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KFtdLCBNMzEsIHRydWUpLCBNMjksIHRydWUpLCBNMzEsIHRydWUpLCBNMzAsIHRydWUpLCBNMzEsIHRydWUpLCBNMzAsIHRydWUpLCBNMzEsIHRydWUpLCBNMzEsIHRydWUpLCBNMzAsIHRydWUpLCBNMzEsIHRydWUpLCBNMzAsIHRydWUpLCBNMzEsIHRydWUpLCBNMzEuc2xpY2UoMCwgNyksIHRydWUpO1xudmFyIE1EQVkzNjVNQVNLID0gX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShbXSwgTTMxLCB0cnVlKSwgTTI4LCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMwLCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMwLCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMwLCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMwLCB0cnVlKSwgTTMxLCB0cnVlKSwgTTMxLnNsaWNlKDAsIDcpLCB0cnVlKTtcbnZhciBOTTI4ID0gcmFuZ2UoLTI4LCAwKTtcbnZhciBOTTI5ID0gcmFuZ2UoLTI5LCAwKTtcbnZhciBOTTMwID0gcmFuZ2UoLTMwLCAwKTtcbnZhciBOTTMxID0gcmFuZ2UoLTMxLCAwKTtcbnZhciBOTURBWTM2Nk1BU0sgPSBfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KFtdLCBOTTMxLCB0cnVlKSwgTk0yOSwgdHJ1ZSksIE5NMzEsIHRydWUpLCBOTTMwLCB0cnVlKSwgTk0zMSwgdHJ1ZSksIE5NMzAsIHRydWUpLCBOTTMxLCB0cnVlKSwgTk0zMSwgdHJ1ZSksIE5NMzAsIHRydWUpLCBOTTMxLCB0cnVlKSwgTk0zMCwgdHJ1ZSksIE5NMzEsIHRydWUpLCBOTTMxLnNsaWNlKDAsIDcpLCB0cnVlKTtcbnZhciBOTURBWTM2NU1BU0sgPSBfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KF9fc3ByZWFkQXJyYXkoX19zcHJlYWRBcnJheShfX3NwcmVhZEFycmF5KFtdLCBOTTMxLCB0cnVlKSwgTk0yOCwgdHJ1ZSksIE5NMzEsIHRydWUpLCBOTTMwLCB0cnVlKSwgTk0zMSwgdHJ1ZSksIE5NMzAsIHRydWUpLCBOTTMxLCB0cnVlKSwgTk0zMSwgdHJ1ZSksIE5NMzAsIHRydWUpLCBOTTMxLCB0cnVlKSwgTk0zMCwgdHJ1ZSksIE5NMzEsIHRydWUpLCBOTTMxLnNsaWNlKDAsIDcpLCB0cnVlKTtcbnZhciBNMzY2UkFOR0UgPSBbMCwgMzEsIDYwLCA5MSwgMTIxLCAxNTIsIDE4MiwgMjEzLCAyNDQsIDI3NCwgMzA1LCAzMzUsIDM2Nl07XG52YXIgTTM2NVJBTkdFID0gWzAsIDMxLCA1OSwgOTAsIDEyMCwgMTUxLCAxODEsIDIxMiwgMjQzLCAyNzMsIDMwNCwgMzM0LCAzNjVdO1xudmFyIFdEQVlNQVNLID0gKGZ1bmN0aW9uICgpIHtcbiAgICB2YXIgd2RheW1hc2sgPSBbXTtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IDU1OyBpKyspXG4gICAgICAgIHdkYXltYXNrID0gd2RheW1hc2suY29uY2F0KHJhbmdlKDcpKTtcbiAgICByZXR1cm4gd2RheW1hc2s7XG59KSgpO1xuZXhwb3J0IHsgV0RBWU1BU0ssIE0zNjVNQVNLLCBNMzY1UkFOR0UsIE0zNjZNQVNLLCBNMzY2UkFOR0UsIE1EQVkzNjVNQVNLLCBNREFZMzY2TUFTSywgTk1EQVkzNjVNQVNLLCBOTURBWTM2Nk1BU0ssIH07XG4vLyMgc291cmNlTWFwcGluZ1VSTD1tYXNrcy5qcy5tYXAiLCIvLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuLy8gaTE4blxuLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbnZhciBFTkdMSVNIID0ge1xuICAgIGRheU5hbWVzOiBbXG4gICAgICAgICdTdW5kYXknLFxuICAgICAgICAnTW9uZGF5JyxcbiAgICAgICAgJ1R1ZXNkYXknLFxuICAgICAgICAnV2VkbmVzZGF5JyxcbiAgICAgICAgJ1RodXJzZGF5JyxcbiAgICAgICAgJ0ZyaWRheScsXG4gICAgICAgICdTYXR1cmRheScsXG4gICAgXSxcbiAgICBtb250aE5hbWVzOiBbXG4gICAgICAgICdKYW51YXJ5JyxcbiAgICAgICAgJ0ZlYnJ1YXJ5JyxcbiAgICAgICAgJ01hcmNoJyxcbiAgICAgICAgJ0FwcmlsJyxcbiAgICAgICAgJ01heScsXG4gICAgICAgICdKdW5lJyxcbiAgICAgICAgJ0p1bHknLFxuICAgICAgICAnQXVndXN0JyxcbiAgICAgICAgJ1NlcHRlbWJlcicsXG4gICAgICAgICdPY3RvYmVyJyxcbiAgICAgICAgJ05vdmVtYmVyJyxcbiAgICAgICAgJ0RlY2VtYmVyJyxcbiAgICBdLFxuICAgIHRva2Vuczoge1xuICAgICAgICBTS0lQOiAvXlsgXFxyXFxuXFx0XSt8XlxcLiQvLFxuICAgICAgICBudW1iZXI6IC9eWzEtOV1bMC05XSovLFxuICAgICAgICBudW1iZXJBc1RleHQ6IC9eKG9uZXx0d298dGhyZWUpL2ksXG4gICAgICAgIGV2ZXJ5OiAvXmV2ZXJ5L2ksXG4gICAgICAgICdkYXkocyknOiAvXmRheXM/L2ksXG4gICAgICAgICd3ZWVrZGF5KHMpJzogL153ZWVrZGF5cz8vaSxcbiAgICAgICAgJ3dlZWsocyknOiAvXndlZWtzPy9pLFxuICAgICAgICAnaG91cihzKSc6IC9eaG91cnM/L2ksXG4gICAgICAgICdtaW51dGUocyknOiAvXm1pbnV0ZXM/L2ksXG4gICAgICAgICdtb250aChzKSc6IC9ebW9udGhzPy9pLFxuICAgICAgICAneWVhcihzKSc6IC9eeWVhcnM/L2ksXG4gICAgICAgIG9uOiAvXihvbnxpbikvaSxcbiAgICAgICAgYXQ6IC9eKGF0KS9pLFxuICAgICAgICB0aGU6IC9edGhlL2ksXG4gICAgICAgIGZpcnN0OiAvXmZpcnN0L2ksXG4gICAgICAgIHNlY29uZDogL15zZWNvbmQvaSxcbiAgICAgICAgdGhpcmQ6IC9edGhpcmQvaSxcbiAgICAgICAgbnRoOiAvXihbMS05XVswLTldKikoXFwufHRofG5kfHJkfHN0KS9pLFxuICAgICAgICBsYXN0OiAvXmxhc3QvaSxcbiAgICAgICAgZm9yOiAvXmZvci9pLFxuICAgICAgICAndGltZShzKSc6IC9edGltZXM/L2ksXG4gICAgICAgIHVudGlsOiAvXih1bik/dGlsL2ksXG4gICAgICAgIG1vbmRheTogL15tbyhuKGRheSk/KT8vaSxcbiAgICAgICAgdHVlc2RheTogL150dShlKHMoZGF5KT8pPyk/L2ksXG4gICAgICAgIHdlZG5lc2RheTogL153ZShkKG4oZXNkYXkpPyk/KT8vaSxcbiAgICAgICAgdGh1cnNkYXk6IC9edGgodShyKHNkYXkpPyk/KT8vaSxcbiAgICAgICAgZnJpZGF5OiAvXmZyKGkoZGF5KT8pPy9pLFxuICAgICAgICBzYXR1cmRheTogL15zYSh0KHVyZGF5KT8pPy9pLFxuICAgICAgICBzdW5kYXk6IC9ec3UobihkYXkpPyk/L2ksXG4gICAgICAgIGphbnVhcnk6IC9eamFuKHVhcnkpPy9pLFxuICAgICAgICBmZWJydWFyeTogL15mZWIocnVhcnkpPy9pLFxuICAgICAgICBtYXJjaDogL15tYXIoY2gpPy9pLFxuICAgICAgICBhcHJpbDogL15hcHIoaWwpPy9pLFxuICAgICAgICBtYXk6IC9ebWF5L2ksXG4gICAgICAgIGp1bmU6IC9eanVuZT8vaSxcbiAgICAgICAganVseTogL15qdWx5Py9pLFxuICAgICAgICBhdWd1c3Q6IC9eYXVnKHVzdCk/L2ksXG4gICAgICAgIHNlcHRlbWJlcjogL15zZXAodChlbWJlcik/KT8vaSxcbiAgICAgICAgb2N0b2JlcjogL15vY3Qob2Jlcik/L2ksXG4gICAgICAgIG5vdmVtYmVyOiAvXm5vdihlbWJlcik/L2ksXG4gICAgICAgIGRlY2VtYmVyOiAvXmRlYyhlbWJlcik/L2ksXG4gICAgICAgIGNvbW1hOiAvXigsXFxzKnwoYW5kfG9yKVxccyopKy9pLFxuICAgIH0sXG59O1xuZXhwb3J0IGRlZmF1bHQgRU5HTElTSDtcbi8vIyBzb3VyY2VNYXBwaW5nVVJMPWkxOG4uanMubWFwIiwiaW1wb3J0IFRvVGV4dCBmcm9tICcuL3RvdGV4dCc7XG5pbXBvcnQgcGFyc2VUZXh0IGZyb20gJy4vcGFyc2V0ZXh0JztcbmltcG9ydCB7IFJSdWxlIH0gZnJvbSAnLi4vcnJ1bGUnO1xuaW1wb3J0IHsgRnJlcXVlbmN5IH0gZnJvbSAnLi4vdHlwZXMnO1xuaW1wb3J0IEVOR0xJU0ggZnJvbSAnLi9pMThuJztcbi8qICFcbiAqIHJydWxlLmpzIC0gTGlicmFyeSBmb3Igd29ya2luZyB3aXRoIHJlY3VycmVuY2UgcnVsZXMgZm9yIGNhbGVuZGFyIGRhdGVzLlxuICogaHR0cHM6Ly9naXRodWIuY29tL2pha3Vicm96dG9jaWwvcnJ1bGVcbiAqXG4gKiBDb3B5cmlnaHQgMjAxMCwgSmFrdWIgUm96dG9jaWwgYW5kIExhcnMgU2Nob25pbmdcbiAqIExpY2VuY2VkIHVuZGVyIHRoZSBCU0QgbGljZW5jZS5cbiAqIGh0dHBzOi8vZ2l0aHViLmNvbS9qYWt1YnJvenRvY2lsL3JydWxlL2Jsb2IvbWFzdGVyL0xJQ0VOQ0VcbiAqXG4gKi9cbi8qKlxuICpcbiAqIEltcGxlbWVudGF0aW9uIG9mIFJSdWxlLmZyb21UZXh0KCkgYW5kIFJSdWxlOjp0b1RleHQoKS5cbiAqXG4gKlxuICogT24gdGhlIGNsaWVudCBzaWRlLCB0aGlzIGZpbGUgbmVlZHMgdG8gYmUgaW5jbHVkZWRcbiAqIHdoZW4gdGhvc2UgZnVuY3Rpb25zIGFyZSB1c2VkLlxuICpcbiAqL1xuLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbi8vIGZyb21UZXh0XG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuLyoqXG4gKiBXaWxsIGJlIGFibGUgdG8gY29udmVydCBzb21lIG9mIHRoZSBiZWxvdyBkZXNjcmliZWQgcnVsZXMgZnJvbVxuICogdGV4dCBmb3JtYXQgdG8gYSBydWxlIG9iamVjdC5cbiAqXG4gKlxuICogUlVMRVNcbiAqXG4gKiBFdmVyeSAoW25dKVxuICogZGF5KHMpXG4gKiB8IFt3ZWVrZGF5XSwgLi4uLCAoYW5kKSBbd2Vla2RheV1cbiAqIHwgd2Vla2RheShzKVxuICogfCB3ZWVrKHMpXG4gKiB8IG1vbnRoKHMpXG4gKiB8IFttb250aF0sIC4uLiwgKGFuZCkgW21vbnRoXVxuICogfCB5ZWFyKHMpXG4gKlxuICpcbiAqIFBsdXMgMCwgMSwgb3IgbXVsdGlwbGUgb2YgdGhlc2U6XG4gKlxuICogb24gW3dlZWtkYXldLCAuLi4sIChvcikgW3dlZWtkYXldIHRoZSBbbW9udGhkYXldLCBbbW9udGhkYXldLCAuLi4gKG9yKSBbbW9udGhkYXldXG4gKlxuICogb24gW3dlZWtkYXldLCAuLi4sIChhbmQpIFt3ZWVrZGF5XVxuICpcbiAqIG9uIHRoZSBbbW9udGhkYXldLCBbbW9udGhkYXldLCAuLi4gKGFuZCkgW21vbnRoZGF5XSAoZGF5IG9mIHRoZSBtb250aClcbiAqXG4gKiBvbiB0aGUgW250aC13ZWVrZGF5XSwgLi4uLCAoYW5kKSBbbnRoLXdlZWtkYXldIChvZiB0aGUgbW9udGgveWVhcilcbiAqXG4gKlxuICogUGx1cyAwIG9yIDEgb2YgdGhlc2U6XG4gKlxuICogZm9yIFtuXSB0aW1lKHMpXG4gKlxuICogdW50aWwgW2RhdGVdXG4gKlxuICogUGx1cyAoLilcbiAqXG4gKlxuICogRGVmaW5pdGVseSBubyBzdXBwb3J0ZWQgZm9yIHBhcnNpbmc6XG4gKlxuICogKGZvciB5ZWFyKTpcbiAqIGluIHdlZWsocykgW25dLCAuLi4sIChhbmQpIFtuXVxuICpcbiAqIG9uIHRoZSBbeWVhcmRheV0sIC4uLiwgKGFuZCkgW25dIGRheSBvZiB0aGUgeWVhclxuICogb24gZGF5IFt5ZWFyZGF5XSwgLi4uLCAoYW5kKSBbbl1cbiAqXG4gKlxuICogTk9OLVRFUk1JTkFMU1xuICpcbiAqIFtuXTogMSwgMiAuLi4sIG9uZSwgdHdvLCB0aHJlZSAuLlxuICogW21vbnRoXTogSmFudWFyeSwgRmVicnVhcnksIE1hcmNoLCBBcHJpbCwgTWF5LCAuLi4gRGVjZW1iZXJcbiAqIFt3ZWVrZGF5XTogTW9uZGF5LCAuLi4gU3VuZGF5XG4gKiBbbnRoLXdlZWtkYXldOiBmaXJzdCBbd2Vla2RheV0sIDJuZCBbd2Vla2RheV0sIC4uLiBsYXN0IFt3ZWVrZGF5XSwgLi4uXG4gKiBbbW9udGhkYXldOiBmaXJzdCwgMS4sIDIuLCAxc3QsIDJuZCwgc2Vjb25kLCAuLi4gMzFzdCwgbGFzdCBkYXksIDJuZCBsYXN0IGRheSwgLi5cbiAqIFtkYXRlXTpcbiAqIC0gW21vbnRoXSAoMC0zMSgsKSAoW3llYXJdKSksXG4gKiAtICh0aGUpIDAtMzEuKDEtMTIuKFt5ZWFyXSkpLFxuICogLSAodGhlKSAwLTMxLygxLTEyLyhbeWVhcl0pKSxcbiAqIC0gW3dlZWtkYXldXG4gKlxuICogW3llYXJdOiAwMDAwLCAwMDAxLCAuLi4gMDEsIDAyLCAuLlxuICpcbiAqIERlZmluaXRlbHkgbm90IHN1cHBvcnRlZCBmb3IgcGFyc2luZzpcbiAqXG4gKiBbeWVhcmRheV06IGZpcnN0LCAxLiwgMi4sIDFzdCwgMm5kLCBzZWNvbmQsIC4uLiAzNjZ0aCwgbGFzdCBkYXksIDJuZCBsYXN0IGRheSwgLi5cbiAqXG4gKiBAcGFyYW0ge1N0cmluZ30gdGV4dFxuICogQHJldHVybiB7T2JqZWN0LCBCb29sZWFufSB0aGUgcnVsZSwgb3IgbnVsbC5cbiAqL1xudmFyIGZyb21UZXh0ID0gZnVuY3Rpb24gKHRleHQsIGxhbmd1YWdlKSB7XG4gICAgaWYgKGxhbmd1YWdlID09PSB2b2lkIDApIHsgbGFuZ3VhZ2UgPSBFTkdMSVNIOyB9XG4gICAgcmV0dXJuIG5ldyBSUnVsZShwYXJzZVRleHQodGV4dCwgbGFuZ3VhZ2UpIHx8IHVuZGVmaW5lZCk7XG59O1xudmFyIGNvbW1vbiA9IFtcbiAgICAnY291bnQnLFxuICAgICd1bnRpbCcsXG4gICAgJ2ludGVydmFsJyxcbiAgICAnYnl3ZWVrZGF5JyxcbiAgICAnYnltb250aGRheScsXG4gICAgJ2J5bW9udGgnLFxuXTtcblRvVGV4dC5JTVBMRU1FTlRFRCA9IFtdO1xuVG9UZXh0LklNUExFTUVOVEVEW0ZyZXF1ZW5jeS5IT1VSTFldID0gY29tbW9uO1xuVG9UZXh0LklNUExFTUVOVEVEW0ZyZXF1ZW5jeS5NSU5VVEVMWV0gPSBjb21tb247XG5Ub1RleHQuSU1QTEVNRU5URURbRnJlcXVlbmN5LkRBSUxZXSA9IFsnYnlob3VyJ10uY29uY2F0KGNvbW1vbik7XG5Ub1RleHQuSU1QTEVNRU5URURbRnJlcXVlbmN5LldFRUtMWV0gPSBjb21tb247XG5Ub1RleHQuSU1QTEVNRU5URURbRnJlcXVlbmN5Lk1PTlRITFldID0gY29tbW9uO1xuVG9UZXh0LklNUExFTUVOVEVEW0ZyZXF1ZW5jeS5ZRUFSTFldID0gWydieXdlZWtubycsICdieXllYXJkYXknXS5jb25jYXQoY29tbW9uKTtcbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG4vLyBFeHBvcnRcbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG52YXIgdG9UZXh0ID0gZnVuY3Rpb24gKHJydWxlLCBnZXR0ZXh0LCBsYW5ndWFnZSwgZGF0ZUZvcm1hdHRlcikge1xuICAgIHJldHVybiBuZXcgVG9UZXh0KHJydWxlLCBnZXR0ZXh0LCBsYW5ndWFnZSwgZGF0ZUZvcm1hdHRlcikudG9TdHJpbmcoKTtcbn07XG52YXIgaXNGdWxseUNvbnZlcnRpYmxlID0gVG9UZXh0LmlzRnVsbHlDb252ZXJ0aWJsZTtcbmV4cG9ydCB7IGZyb21UZXh0LCBwYXJzZVRleHQsIGlzRnVsbHlDb252ZXJ0aWJsZSwgdG9UZXh0IH07XG4vLyMgc291cmNlTWFwcGluZ1VSTD1pbmRleC5qcy5tYXAiLCJpbXBvcnQgRU5HTElTSCBmcm9tICcuL2kxOG4nO1xuaW1wb3J0IHsgUlJ1bGUgfSBmcm9tICcuLi9ycnVsZSc7XG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuLy8gUGFyc2VyXG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxudmFyIFBhcnNlciA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBQYXJzZXIocnVsZXMpIHtcbiAgICAgICAgdGhpcy5kb25lID0gdHJ1ZTtcbiAgICAgICAgdGhpcy5ydWxlcyA9IHJ1bGVzO1xuICAgIH1cbiAgICBQYXJzZXIucHJvdG90eXBlLnN0YXJ0ID0gZnVuY3Rpb24gKHRleHQpIHtcbiAgICAgICAgdGhpcy50ZXh0ID0gdGV4dDtcbiAgICAgICAgdGhpcy5kb25lID0gZmFsc2U7XG4gICAgICAgIHJldHVybiB0aGlzLm5leHRTeW1ib2woKTtcbiAgICB9O1xuICAgIFBhcnNlci5wcm90b3R5cGUuaXNEb25lID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5kb25lICYmIHRoaXMuc3ltYm9sID09PSBudWxsO1xuICAgIH07XG4gICAgUGFyc2VyLnByb3RvdHlwZS5uZXh0U3ltYm9sID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgYmVzdDtcbiAgICAgICAgdmFyIGJlc3RTeW1ib2w7XG4gICAgICAgIHRoaXMuc3ltYm9sID0gbnVsbDtcbiAgICAgICAgdGhpcy52YWx1ZSA9IG51bGw7XG4gICAgICAgIGRvIHtcbiAgICAgICAgICAgIGlmICh0aGlzLmRvbmUpXG4gICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgdmFyIHJ1bGUgPSB2b2lkIDA7XG4gICAgICAgICAgICBiZXN0ID0gbnVsbDtcbiAgICAgICAgICAgIGZvciAodmFyIG5hbWVfMSBpbiB0aGlzLnJ1bGVzKSB7XG4gICAgICAgICAgICAgICAgcnVsZSA9IHRoaXMucnVsZXNbbmFtZV8xXTtcbiAgICAgICAgICAgICAgICB2YXIgbWF0Y2ggPSBydWxlLmV4ZWModGhpcy50ZXh0KTtcbiAgICAgICAgICAgICAgICBpZiAobWF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGJlc3QgPT09IG51bGwgfHwgbWF0Y2hbMF0ubGVuZ3RoID4gYmVzdFswXS5sZW5ndGgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGJlc3QgPSBtYXRjaDtcbiAgICAgICAgICAgICAgICAgICAgICAgIGJlc3RTeW1ib2wgPSBuYW1lXzE7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAoYmVzdCAhPSBudWxsKSB7XG4gICAgICAgICAgICAgICAgdGhpcy50ZXh0ID0gdGhpcy50ZXh0LnN1YnN0cihiZXN0WzBdLmxlbmd0aCk7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMudGV4dCA9PT0gJycpXG4gICAgICAgICAgICAgICAgICAgIHRoaXMuZG9uZSA9IHRydWU7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAoYmVzdCA9PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5kb25lID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICB0aGlzLnN5bWJvbCA9IG51bGw7XG4gICAgICAgICAgICAgICAgdGhpcy52YWx1ZSA9IG51bGw7XG4gICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgfVxuICAgICAgICB9IHdoaWxlIChiZXN0U3ltYm9sID09PSAnU0tJUCcpO1xuICAgICAgICB0aGlzLnN5bWJvbCA9IGJlc3RTeW1ib2w7XG4gICAgICAgIHRoaXMudmFsdWUgPSBiZXN0O1xuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9O1xuICAgIFBhcnNlci5wcm90b3R5cGUuYWNjZXB0ID0gZnVuY3Rpb24gKG5hbWUpIHtcbiAgICAgICAgaWYgKHRoaXMuc3ltYm9sID09PSBuYW1lKSB7XG4gICAgICAgICAgICBpZiAodGhpcy52YWx1ZSkge1xuICAgICAgICAgICAgICAgIHZhciB2ID0gdGhpcy52YWx1ZTtcbiAgICAgICAgICAgICAgICB0aGlzLm5leHRTeW1ib2woKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gdjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRoaXMubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgIH07XG4gICAgUGFyc2VyLnByb3RvdHlwZS5hY2NlcHROdW1iZXIgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLmFjY2VwdCgnbnVtYmVyJyk7XG4gICAgfTtcbiAgICBQYXJzZXIucHJvdG90eXBlLmV4cGVjdCA9IGZ1bmN0aW9uIChuYW1lKSB7XG4gICAgICAgIGlmICh0aGlzLmFjY2VwdChuYW1lKSlcbiAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ2V4cGVjdGVkICcgKyBuYW1lICsgJyBidXQgZm91bmQgJyArIHRoaXMuc3ltYm9sKTtcbiAgICB9O1xuICAgIHJldHVybiBQYXJzZXI7XG59KCkpO1xuZXhwb3J0IGRlZmF1bHQgZnVuY3Rpb24gcGFyc2VUZXh0KHRleHQsIGxhbmd1YWdlKSB7XG4gICAgaWYgKGxhbmd1YWdlID09PSB2b2lkIDApIHsgbGFuZ3VhZ2UgPSBFTkdMSVNIOyB9XG4gICAgdmFyIG9wdGlvbnMgPSB7fTtcbiAgICB2YXIgdHRyID0gbmV3IFBhcnNlcihsYW5ndWFnZS50b2tlbnMpO1xuICAgIGlmICghdHRyLnN0YXJ0KHRleHQpKVxuICAgICAgICByZXR1cm4gbnVsbDtcbiAgICBTKCk7XG4gICAgcmV0dXJuIG9wdGlvbnM7XG4gICAgZnVuY3Rpb24gUygpIHtcbiAgICAgICAgLy8gZXZlcnkgW25dXG4gICAgICAgIHR0ci5leHBlY3QoJ2V2ZXJ5Jyk7XG4gICAgICAgIHZhciBuID0gdHRyLmFjY2VwdE51bWJlcigpO1xuICAgICAgICBpZiAobilcbiAgICAgICAgICAgIG9wdGlvbnMuaW50ZXJ2YWwgPSBwYXJzZUludChuWzBdLCAxMCk7XG4gICAgICAgIGlmICh0dHIuaXNEb25lKCkpXG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1VuZXhwZWN0ZWQgZW5kJyk7XG4gICAgICAgIHN3aXRjaCAodHRyLnN5bWJvbCkge1xuICAgICAgICAgICAgY2FzZSAnZGF5KHMpJzpcbiAgICAgICAgICAgICAgICBvcHRpb25zLmZyZXEgPSBSUnVsZS5EQUlMWTtcbiAgICAgICAgICAgICAgICBpZiAodHRyLm5leHRTeW1ib2woKSkge1xuICAgICAgICAgICAgICAgICAgICBBVCgpO1xuICAgICAgICAgICAgICAgICAgICBGKCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgLy8gRklYTUUgTm90ZTogZXZlcnkgMiB3ZWVrZGF5cyAhPSBldmVyeSB0d28gd2Vla3Mgb24gd2Vla2RheXMuXG4gICAgICAgICAgICAvLyBEQUlMWSBvbiB3ZWVrZGF5cyBpcyBub3QgYSB2YWxpZCBydWxlXG4gICAgICAgICAgICBjYXNlICd3ZWVrZGF5KHMpJzpcbiAgICAgICAgICAgICAgICBvcHRpb25zLmZyZXEgPSBSUnVsZS5XRUVLTFk7XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtkYXkgPSBbUlJ1bGUuTU8sIFJSdWxlLlRVLCBSUnVsZS5XRSwgUlJ1bGUuVEgsIFJSdWxlLkZSXTtcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIEYoKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgJ3dlZWsocyknOlxuICAgICAgICAgICAgICAgIG9wdGlvbnMuZnJlcSA9IFJSdWxlLldFRUtMWTtcbiAgICAgICAgICAgICAgICBpZiAodHRyLm5leHRTeW1ib2woKSkge1xuICAgICAgICAgICAgICAgICAgICBPTigpO1xuICAgICAgICAgICAgICAgICAgICBGKCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnaG91cihzKSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5mcmVxID0gUlJ1bGUuSE9VUkxZO1xuICAgICAgICAgICAgICAgIGlmICh0dHIubmV4dFN5bWJvbCgpKSB7XG4gICAgICAgICAgICAgICAgICAgIE9OKCk7XG4gICAgICAgICAgICAgICAgICAgIEYoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdtaW51dGUocyknOlxuICAgICAgICAgICAgICAgIG9wdGlvbnMuZnJlcSA9IFJSdWxlLk1JTlVURUxZO1xuICAgICAgICAgICAgICAgIGlmICh0dHIubmV4dFN5bWJvbCgpKSB7XG4gICAgICAgICAgICAgICAgICAgIE9OKCk7XG4gICAgICAgICAgICAgICAgICAgIEYoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdtb250aChzKSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5mcmVxID0gUlJ1bGUuTU9OVEhMWTtcbiAgICAgICAgICAgICAgICBpZiAodHRyLm5leHRTeW1ib2woKSkge1xuICAgICAgICAgICAgICAgICAgICBPTigpO1xuICAgICAgICAgICAgICAgICAgICBGKCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAneWVhcihzKSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5mcmVxID0gUlJ1bGUuWUVBUkxZO1xuICAgICAgICAgICAgICAgIGlmICh0dHIubmV4dFN5bWJvbCgpKSB7XG4gICAgICAgICAgICAgICAgICAgIE9OKCk7XG4gICAgICAgICAgICAgICAgICAgIEYoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdtb25kYXknOlxuICAgICAgICAgICAgY2FzZSAndHVlc2RheSc6XG4gICAgICAgICAgICBjYXNlICd3ZWRuZXNkYXknOlxuICAgICAgICAgICAgY2FzZSAndGh1cnNkYXknOlxuICAgICAgICAgICAgY2FzZSAnZnJpZGF5JzpcbiAgICAgICAgICAgIGNhc2UgJ3NhdHVyZGF5JzpcbiAgICAgICAgICAgIGNhc2UgJ3N1bmRheSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5mcmVxID0gUlJ1bGUuV0VFS0xZO1xuICAgICAgICAgICAgICAgIHZhciBrZXkgPSB0dHIuc3ltYm9sXG4gICAgICAgICAgICAgICAgICAgIC5zdWJzdHIoMCwgMilcbiAgICAgICAgICAgICAgICAgICAgLnRvVXBwZXJDYXNlKCk7XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtkYXkgPSBbUlJ1bGVba2V5XV07XG4gICAgICAgICAgICAgICAgaWYgKCF0dHIubmV4dFN5bWJvbCgpKVxuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgLy8gVE9ETyBjaGVjayBmb3IgZHVwbGljYXRlc1xuICAgICAgICAgICAgICAgIHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmICh0dHIuaXNEb25lKCkpXG4gICAgICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1VuZXhwZWN0ZWQgZW5kJyk7XG4gICAgICAgICAgICAgICAgICAgIHZhciB3a2QgPSBkZWNvZGVXS0QoKTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKCF3a2QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignVW5leHBlY3RlZCBzeW1ib2wgJyArIHR0ci5zeW1ib2wgKyAnLCBleHBlY3RlZCB3ZWVrZGF5Jyk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtkYXkucHVzaChSUnVsZVt3a2RdKTtcbiAgICAgICAgICAgICAgICAgICAgdHRyLm5leHRTeW1ib2woKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgTURBWXMoKTtcbiAgICAgICAgICAgICAgICBGKCk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdqYW51YXJ5JzpcbiAgICAgICAgICAgIGNhc2UgJ2ZlYnJ1YXJ5JzpcbiAgICAgICAgICAgIGNhc2UgJ21hcmNoJzpcbiAgICAgICAgICAgIGNhc2UgJ2FwcmlsJzpcbiAgICAgICAgICAgIGNhc2UgJ21heSc6XG4gICAgICAgICAgICBjYXNlICdqdW5lJzpcbiAgICAgICAgICAgIGNhc2UgJ2p1bHknOlxuICAgICAgICAgICAgY2FzZSAnYXVndXN0JzpcbiAgICAgICAgICAgIGNhc2UgJ3NlcHRlbWJlcic6XG4gICAgICAgICAgICBjYXNlICdvY3RvYmVyJzpcbiAgICAgICAgICAgIGNhc2UgJ25vdmVtYmVyJzpcbiAgICAgICAgICAgIGNhc2UgJ2RlY2VtYmVyJzpcbiAgICAgICAgICAgICAgICBvcHRpb25zLmZyZXEgPSBSUnVsZS5ZRUFSTFk7XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieW1vbnRoID0gW2RlY29kZU0oKV07XG4gICAgICAgICAgICAgICAgaWYgKCF0dHIubmV4dFN5bWJvbCgpKVxuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgLy8gVE9ETyBjaGVjayBmb3IgZHVwbGljYXRlc1xuICAgICAgICAgICAgICAgIHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmICh0dHIuaXNEb25lKCkpXG4gICAgICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1VuZXhwZWN0ZWQgZW5kJyk7XG4gICAgICAgICAgICAgICAgICAgIHZhciBtID0gZGVjb2RlTSgpO1xuICAgICAgICAgICAgICAgICAgICBpZiAoIW0pIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignVW5leHBlY3RlZCBzeW1ib2wgJyArIHR0ci5zeW1ib2wgKyAnLCBleHBlY3RlZCBtb250aCcpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIG9wdGlvbnMuYnltb250aC5wdXNoKG0pO1xuICAgICAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBPTigpO1xuICAgICAgICAgICAgICAgIEYoKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdVbmtub3duIHN5bWJvbCcpO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIE9OKCkge1xuICAgICAgICB2YXIgb24gPSB0dHIuYWNjZXB0KCdvbicpO1xuICAgICAgICB2YXIgdGhlID0gdHRyLmFjY2VwdCgndGhlJyk7XG4gICAgICAgIGlmICghKG9uIHx8IHRoZSkpXG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIGRvIHtcbiAgICAgICAgICAgIHZhciBudGggPSBkZWNvZGVOVEgoKTtcbiAgICAgICAgICAgIHZhciB3a2QgPSBkZWNvZGVXS0QoKTtcbiAgICAgICAgICAgIHZhciBtID0gZGVjb2RlTSgpO1xuICAgICAgICAgICAgLy8gbnRoIDx3ZWVrZGF5PiB8IDx3ZWVrZGF5PlxuICAgICAgICAgICAgaWYgKG50aCkge1xuICAgICAgICAgICAgICAgIC8vIHR0ci5uZXh0U3ltYm9sKClcbiAgICAgICAgICAgICAgICBpZiAod2tkKSB7XG4gICAgICAgICAgICAgICAgICAgIHR0ci5uZXh0U3ltYm9sKCk7XG4gICAgICAgICAgICAgICAgICAgIGlmICghb3B0aW9ucy5ieXdlZWtkYXkpXG4gICAgICAgICAgICAgICAgICAgICAgICBvcHRpb25zLmJ5d2Vla2RheSA9IFtdO1xuICAgICAgICAgICAgICAgICAgICBvcHRpb25zLmJ5d2Vla2RheS5wdXNoKFJSdWxlW3drZF0ubnRoKG50aCkpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKCFvcHRpb25zLmJ5bW9udGhkYXkpXG4gICAgICAgICAgICAgICAgICAgICAgICBvcHRpb25zLmJ5bW9udGhkYXkgPSBbXTtcbiAgICAgICAgICAgICAgICAgICAgb3B0aW9ucy5ieW1vbnRoZGF5LnB1c2gobnRoKTtcbiAgICAgICAgICAgICAgICAgICAgdHRyLmFjY2VwdCgnZGF5KHMpJyk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIC8vIDx3ZWVrZGF5PlxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAod2tkKSB7XG4gICAgICAgICAgICAgICAgdHRyLm5leHRTeW1ib2woKTtcbiAgICAgICAgICAgICAgICBpZiAoIW9wdGlvbnMuYnl3ZWVrZGF5KVxuICAgICAgICAgICAgICAgICAgICBvcHRpb25zLmJ5d2Vla2RheSA9IFtdO1xuICAgICAgICAgICAgICAgIG9wdGlvbnMuYnl3ZWVrZGF5LnB1c2goUlJ1bGVbd2tkXSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmICh0dHIuc3ltYm9sID09PSAnd2Vla2RheShzKScpIHtcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIGlmICghb3B0aW9ucy5ieXdlZWtkYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtkYXkgPSBbUlJ1bGUuTU8sIFJSdWxlLlRVLCBSUnVsZS5XRSwgUlJ1bGUuVEgsIFJSdWxlLkZSXTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmICh0dHIuc3ltYm9sID09PSAnd2VlayhzKScpIHtcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIHZhciBuID0gdHRyLmFjY2VwdE51bWJlcigpO1xuICAgICAgICAgICAgICAgIGlmICghbikge1xuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1VuZXhwZWN0ZWQgc3ltYm9sICcgKyB0dHIuc3ltYm9sICsgJywgZXhwZWN0ZWQgd2VlayBudW1iZXInKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtubyA9IFtwYXJzZUludChuWzBdLCAxMCldO1xuICAgICAgICAgICAgICAgIHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpKSB7XG4gICAgICAgICAgICAgICAgICAgIG4gPSB0dHIuYWNjZXB0TnVtYmVyKCk7XG4gICAgICAgICAgICAgICAgICAgIGlmICghbikge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdVbmV4cGVjdGVkIHN5bWJvbCAnICsgdHRyLnN5bWJvbCArICc7IGV4cGVjdGVkIG1vbnRoZGF5Jyk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtuby5wdXNoKHBhcnNlSW50KG5bMF0sIDEwKSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAobSkge1xuICAgICAgICAgICAgICAgIHR0ci5uZXh0U3ltYm9sKCk7XG4gICAgICAgICAgICAgICAgaWYgKCFvcHRpb25zLmJ5bW9udGgpXG4gICAgICAgICAgICAgICAgICAgIG9wdGlvbnMuYnltb250aCA9IFtdO1xuICAgICAgICAgICAgICAgIG9wdGlvbnMuYnltb250aC5wdXNoKG0pO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgfVxuICAgICAgICB9IHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpIHx8IHR0ci5hY2NlcHQoJ3RoZScpIHx8IHR0ci5hY2NlcHQoJ29uJykpO1xuICAgIH1cbiAgICBmdW5jdGlvbiBBVCgpIHtcbiAgICAgICAgdmFyIGF0ID0gdHRyLmFjY2VwdCgnYXQnKTtcbiAgICAgICAgaWYgKCFhdClcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgZG8ge1xuICAgICAgICAgICAgdmFyIG4gPSB0dHIuYWNjZXB0TnVtYmVyKCk7XG4gICAgICAgICAgICBpZiAoIW4pIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1VuZXhwZWN0ZWQgc3ltYm9sICcgKyB0dHIuc3ltYm9sICsgJywgZXhwZWN0ZWQgaG91cicpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgb3B0aW9ucy5ieWhvdXIgPSBbcGFyc2VJbnQoblswXSwgMTApXTtcbiAgICAgICAgICAgIHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpKSB7XG4gICAgICAgICAgICAgICAgbiA9IHR0ci5hY2NlcHROdW1iZXIoKTtcbiAgICAgICAgICAgICAgICBpZiAoIW4pIHtcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdVbmV4cGVjdGVkIHN5bWJvbCAnICsgdHRyLnN5bWJvbCArICc7IGV4cGVjdGVkIGhvdXInKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieWhvdXIucHVzaChwYXJzZUludChuWzBdLCAxMCkpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9IHdoaWxlICh0dHIuYWNjZXB0KCdjb21tYScpIHx8IHR0ci5hY2NlcHQoJ2F0JykpO1xuICAgIH1cbiAgICBmdW5jdGlvbiBkZWNvZGVNKCkge1xuICAgICAgICBzd2l0Y2ggKHR0ci5zeW1ib2wpIHtcbiAgICAgICAgICAgIGNhc2UgJ2phbnVhcnknOlxuICAgICAgICAgICAgICAgIHJldHVybiAxO1xuICAgICAgICAgICAgY2FzZSAnZmVicnVhcnknOlxuICAgICAgICAgICAgICAgIHJldHVybiAyO1xuICAgICAgICAgICAgY2FzZSAnbWFyY2gnOlxuICAgICAgICAgICAgICAgIHJldHVybiAzO1xuICAgICAgICAgICAgY2FzZSAnYXByaWwnOlxuICAgICAgICAgICAgICAgIHJldHVybiA0O1xuICAgICAgICAgICAgY2FzZSAnbWF5JzpcbiAgICAgICAgICAgICAgICByZXR1cm4gNTtcbiAgICAgICAgICAgIGNhc2UgJ2p1bmUnOlxuICAgICAgICAgICAgICAgIHJldHVybiA2O1xuICAgICAgICAgICAgY2FzZSAnanVseSc6XG4gICAgICAgICAgICAgICAgcmV0dXJuIDc7XG4gICAgICAgICAgICBjYXNlICdhdWd1c3QnOlxuICAgICAgICAgICAgICAgIHJldHVybiA4O1xuICAgICAgICAgICAgY2FzZSAnc2VwdGVtYmVyJzpcbiAgICAgICAgICAgICAgICByZXR1cm4gOTtcbiAgICAgICAgICAgIGNhc2UgJ29jdG9iZXInOlxuICAgICAgICAgICAgICAgIHJldHVybiAxMDtcbiAgICAgICAgICAgIGNhc2UgJ25vdmVtYmVyJzpcbiAgICAgICAgICAgICAgICByZXR1cm4gMTE7XG4gICAgICAgICAgICBjYXNlICdkZWNlbWJlcic6XG4gICAgICAgICAgICAgICAgcmV0dXJuIDEyO1xuICAgICAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gZGVjb2RlV0tEKCkge1xuICAgICAgICBzd2l0Y2ggKHR0ci5zeW1ib2wpIHtcbiAgICAgICAgICAgIGNhc2UgJ21vbmRheSc6XG4gICAgICAgICAgICBjYXNlICd0dWVzZGF5JzpcbiAgICAgICAgICAgIGNhc2UgJ3dlZG5lc2RheSc6XG4gICAgICAgICAgICBjYXNlICd0aHVyc2RheSc6XG4gICAgICAgICAgICBjYXNlICdmcmlkYXknOlxuICAgICAgICAgICAgY2FzZSAnc2F0dXJkYXknOlxuICAgICAgICAgICAgY2FzZSAnc3VuZGF5JzpcbiAgICAgICAgICAgICAgICByZXR1cm4gdHRyLnN5bWJvbC5zdWJzdHIoMCwgMikudG9VcHBlckNhc2UoKTtcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG4gICAgfVxuICAgIGZ1bmN0aW9uIGRlY29kZU5USCgpIHtcbiAgICAgICAgc3dpdGNoICh0dHIuc3ltYm9sKSB7XG4gICAgICAgICAgICBjYXNlICdsYXN0JzpcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIHJldHVybiAtMTtcbiAgICAgICAgICAgIGNhc2UgJ2ZpcnN0JzpcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIHJldHVybiAxO1xuICAgICAgICAgICAgY2FzZSAnc2Vjb25kJzpcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIHJldHVybiB0dHIuYWNjZXB0KCdsYXN0JykgPyAtMiA6IDI7XG4gICAgICAgICAgICBjYXNlICd0aGlyZCc6XG4gICAgICAgICAgICAgICAgdHRyLm5leHRTeW1ib2woKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gdHRyLmFjY2VwdCgnbGFzdCcpID8gLTMgOiAzO1xuICAgICAgICAgICAgY2FzZSAnbnRoJzpcbiAgICAgICAgICAgICAgICB2YXIgdiA9IHBhcnNlSW50KHR0ci52YWx1ZVsxXSwgMTApO1xuICAgICAgICAgICAgICAgIGlmICh2IDwgLTM2NiB8fCB2ID4gMzY2KVxuICAgICAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ050aCBvdXQgb2YgcmFuZ2U6ICcgKyB2KTtcbiAgICAgICAgICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICAgICAgICAgIHJldHVybiB0dHIuYWNjZXB0KCdsYXN0JykgPyAtdiA6IHY7XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBmdW5jdGlvbiBNREFZcygpIHtcbiAgICAgICAgdHRyLmFjY2VwdCgnb24nKTtcbiAgICAgICAgdHRyLmFjY2VwdCgndGhlJyk7XG4gICAgICAgIHZhciBudGggPSBkZWNvZGVOVEgoKTtcbiAgICAgICAgaWYgKCFudGgpXG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIG9wdGlvbnMuYnltb250aGRheSA9IFtudGhdO1xuICAgICAgICB0dHIubmV4dFN5bWJvbCgpO1xuICAgICAgICB3aGlsZSAodHRyLmFjY2VwdCgnY29tbWEnKSkge1xuICAgICAgICAgICAgbnRoID0gZGVjb2RlTlRIKCk7XG4gICAgICAgICAgICBpZiAoIW50aCkge1xuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignVW5leHBlY3RlZCBzeW1ib2wgJyArIHR0ci5zeW1ib2wgKyAnOyBleHBlY3RlZCBtb250aGRheScpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgb3B0aW9ucy5ieW1vbnRoZGF5LnB1c2gobnRoKTtcbiAgICAgICAgICAgIHR0ci5uZXh0U3ltYm9sKCk7XG4gICAgICAgIH1cbiAgICB9XG4gICAgZnVuY3Rpb24gRigpIHtcbiAgICAgICAgaWYgKHR0ci5zeW1ib2wgPT09ICd1bnRpbCcpIHtcbiAgICAgICAgICAgIHZhciBkYXRlID0gRGF0ZS5wYXJzZSh0dHIudGV4dCk7XG4gICAgICAgICAgICBpZiAoIWRhdGUpXG4gICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdDYW5ub3QgcGFyc2UgdW50aWwgZGF0ZTonICsgdHRyLnRleHQpO1xuICAgICAgICAgICAgb3B0aW9ucy51bnRpbCA9IG5ldyBEYXRlKGRhdGUpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHR0ci5hY2NlcHQoJ2ZvcicpKSB7XG4gICAgICAgICAgICBvcHRpb25zLmNvdW50ID0gcGFyc2VJbnQodHRyLnZhbHVlWzBdLCAxMCk7XG4gICAgICAgICAgICB0dHIuZXhwZWN0KCdudW1iZXInKTtcbiAgICAgICAgICAgIC8vIHR0ci5leHBlY3QoJ3RpbWVzJylcbiAgICAgICAgfVxuICAgIH1cbn1cbi8vIyBzb3VyY2VNYXBwaW5nVVJMPXBhcnNldGV4dC5qcy5tYXAiLCJpbXBvcnQgRU5HTElTSCBmcm9tICcuL2kxOG4nO1xuaW1wb3J0IHsgUlJ1bGUgfSBmcm9tICcuLi9ycnVsZSc7XG5pbXBvcnQgeyBpc0FycmF5LCBpc051bWJlciwgaXNQcmVzZW50IH0gZnJvbSAnLi4vaGVscGVycyc7XG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuLy8gSGVscGVyIGZ1bmN0aW9uc1xuLy8gPT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT1cbi8qKlxuICogUmV0dXJuIHRydWUgaWYgYSB2YWx1ZSBpcyBpbiBhbiBhcnJheVxuICovXG52YXIgY29udGFpbnMgPSBmdW5jdGlvbiAoYXJyLCB2YWwpIHtcbiAgICByZXR1cm4gYXJyLmluZGV4T2YodmFsKSAhPT0gLTE7XG59O1xudmFyIGRlZmF1bHRHZXRUZXh0ID0gZnVuY3Rpb24gKGlkKSB7IHJldHVybiBpZC50b1N0cmluZygpOyB9O1xudmFyIGRlZmF1bHREYXRlRm9ybWF0dGVyID0gZnVuY3Rpb24gKHllYXIsIG1vbnRoLCBkYXkpIHsgcmV0dXJuIFwiXCIuY29uY2F0KG1vbnRoLCBcIiBcIikuY29uY2F0KGRheSwgXCIsIFwiKS5jb25jYXQoeWVhcik7IH07XG4vKipcbiAqXG4gKiBAcGFyYW0ge1JSdWxlfSBycnVsZVxuICogT3B0aW9uYWw6XG4gKiBAcGFyYW0ge0Z1bmN0aW9ufSBnZXR0ZXh0IGZ1bmN0aW9uXG4gKiBAcGFyYW0ge09iamVjdH0gbGFuZ3VhZ2UgZGVmaW5pdGlvblxuICogQGNvbnN0cnVjdG9yXG4gKi9cbnZhciBUb1RleHQgPSAvKiogQGNsYXNzICovIChmdW5jdGlvbiAoKSB7XG4gICAgZnVuY3Rpb24gVG9UZXh0KHJydWxlLCBnZXR0ZXh0LCBsYW5ndWFnZSwgZGF0ZUZvcm1hdHRlcikge1xuICAgICAgICBpZiAoZ2V0dGV4dCA9PT0gdm9pZCAwKSB7IGdldHRleHQgPSBkZWZhdWx0R2V0VGV4dDsgfVxuICAgICAgICBpZiAobGFuZ3VhZ2UgPT09IHZvaWQgMCkgeyBsYW5ndWFnZSA9IEVOR0xJU0g7IH1cbiAgICAgICAgaWYgKGRhdGVGb3JtYXR0ZXIgPT09IHZvaWQgMCkgeyBkYXRlRm9ybWF0dGVyID0gZGVmYXVsdERhdGVGb3JtYXR0ZXI7IH1cbiAgICAgICAgdGhpcy50ZXh0ID0gW107XG4gICAgICAgIHRoaXMubGFuZ3VhZ2UgPSBsYW5ndWFnZSB8fCBFTkdMSVNIO1xuICAgICAgICB0aGlzLmdldHRleHQgPSBnZXR0ZXh0O1xuICAgICAgICB0aGlzLmRhdGVGb3JtYXR0ZXIgPSBkYXRlRm9ybWF0dGVyO1xuICAgICAgICB0aGlzLnJydWxlID0gcnJ1bGU7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9IHJydWxlLm9wdGlvbnM7XG4gICAgICAgIHRoaXMub3JpZ09wdGlvbnMgPSBycnVsZS5vcmlnT3B0aW9ucztcbiAgICAgICAgaWYgKHRoaXMub3JpZ09wdGlvbnMuYnltb250aGRheSkge1xuICAgICAgICAgICAgdmFyIGJ5bW9udGhkYXkgPSBbXS5jb25jYXQodGhpcy5vcHRpb25zLmJ5bW9udGhkYXkpO1xuICAgICAgICAgICAgdmFyIGJ5bm1vbnRoZGF5ID0gW10uY29uY2F0KHRoaXMub3B0aW9ucy5ieW5tb250aGRheSk7XG4gICAgICAgICAgICBieW1vbnRoZGF5LnNvcnQoZnVuY3Rpb24gKGEsIGIpIHsgcmV0dXJuIGEgLSBiOyB9KTtcbiAgICAgICAgICAgIGJ5bm1vbnRoZGF5LnNvcnQoZnVuY3Rpb24gKGEsIGIpIHsgcmV0dXJuIGIgLSBhOyB9KTtcbiAgICAgICAgICAgIC8vIDEsIDIsIDMsIC4uLCAtNSwgLTQsIC0zLCAuLlxuICAgICAgICAgICAgdGhpcy5ieW1vbnRoZGF5ID0gYnltb250aGRheS5jb25jYXQoYnlubW9udGhkYXkpO1xuICAgICAgICAgICAgaWYgKCF0aGlzLmJ5bW9udGhkYXkubGVuZ3RoKVxuICAgICAgICAgICAgICAgIHRoaXMuYnltb250aGRheSA9IG51bGw7XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGlzUHJlc2VudCh0aGlzLm9yaWdPcHRpb25zLmJ5d2Vla2RheSkpIHtcbiAgICAgICAgICAgIHZhciBieXdlZWtkYXkgPSAhaXNBcnJheSh0aGlzLm9yaWdPcHRpb25zLmJ5d2Vla2RheSlcbiAgICAgICAgICAgICAgICA/IFt0aGlzLm9yaWdPcHRpb25zLmJ5d2Vla2RheV1cbiAgICAgICAgICAgICAgICA6IHRoaXMub3JpZ09wdGlvbnMuYnl3ZWVrZGF5O1xuICAgICAgICAgICAgdmFyIGRheXMgPSBTdHJpbmcoYnl3ZWVrZGF5KTtcbiAgICAgICAgICAgIHRoaXMuYnl3ZWVrZGF5ID0ge1xuICAgICAgICAgICAgICAgIGFsbFdlZWtzOiBieXdlZWtkYXkuZmlsdGVyKGZ1bmN0aW9uICh3ZWVrZGF5KSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiAhd2Vla2RheS5uO1xuICAgICAgICAgICAgICAgIH0pLFxuICAgICAgICAgICAgICAgIHNvbWVXZWVrczogYnl3ZWVrZGF5LmZpbHRlcihmdW5jdGlvbiAod2Vla2RheSkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gQm9vbGVhbih3ZWVrZGF5Lm4pO1xuICAgICAgICAgICAgICAgIH0pLFxuICAgICAgICAgICAgICAgIGlzV2Vla2RheXM6IGRheXMuaW5kZXhPZignTU8nKSAhPT0gLTEgJiZcbiAgICAgICAgICAgICAgICAgICAgZGF5cy5pbmRleE9mKCdUVScpICE9PSAtMSAmJlxuICAgICAgICAgICAgICAgICAgICBkYXlzLmluZGV4T2YoJ1dFJykgIT09IC0xICYmXG4gICAgICAgICAgICAgICAgICAgIGRheXMuaW5kZXhPZignVEgnKSAhPT0gLTEgJiZcbiAgICAgICAgICAgICAgICAgICAgZGF5cy5pbmRleE9mKCdGUicpICE9PSAtMSAmJlxuICAgICAgICAgICAgICAgICAgICBkYXlzLmluZGV4T2YoJ1NBJykgPT09IC0xICYmXG4gICAgICAgICAgICAgICAgICAgIGRheXMuaW5kZXhPZignU1UnKSA9PT0gLTEsXG4gICAgICAgICAgICAgICAgaXNFdmVyeURheTogZGF5cy5pbmRleE9mKCdNTycpICE9PSAtMSAmJlxuICAgICAgICAgICAgICAgICAgICBkYXlzLmluZGV4T2YoJ1RVJykgIT09IC0xICYmXG4gICAgICAgICAgICAgICAgICAgIGRheXMuaW5kZXhPZignV0UnKSAhPT0gLTEgJiZcbiAgICAgICAgICAgICAgICAgICAgZGF5cy5pbmRleE9mKCdUSCcpICE9PSAtMSAmJlxuICAgICAgICAgICAgICAgICAgICBkYXlzLmluZGV4T2YoJ0ZSJykgIT09IC0xICYmXG4gICAgICAgICAgICAgICAgICAgIGRheXMuaW5kZXhPZignU0EnKSAhPT0gLTEgJiZcbiAgICAgICAgICAgICAgICAgICAgZGF5cy5pbmRleE9mKCdTVScpICE9PSAtMSxcbiAgICAgICAgICAgIH07XG4gICAgICAgICAgICB2YXIgc29ydFdlZWtEYXlzID0gZnVuY3Rpb24gKGEsIGIpIHtcbiAgICAgICAgICAgICAgICByZXR1cm4gYS53ZWVrZGF5IC0gYi53ZWVrZGF5O1xuICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIHRoaXMuYnl3ZWVrZGF5LmFsbFdlZWtzLnNvcnQoc29ydFdlZWtEYXlzKTtcbiAgICAgICAgICAgIHRoaXMuYnl3ZWVrZGF5LnNvbWVXZWVrcy5zb3J0KHNvcnRXZWVrRGF5cyk7XG4gICAgICAgICAgICBpZiAoIXRoaXMuYnl3ZWVrZGF5LmFsbFdlZWtzLmxlbmd0aClcbiAgICAgICAgICAgICAgICB0aGlzLmJ5d2Vla2RheS5hbGxXZWVrcyA9IG51bGw7XG4gICAgICAgICAgICBpZiAoIXRoaXMuYnl3ZWVrZGF5LnNvbWVXZWVrcy5sZW5ndGgpXG4gICAgICAgICAgICAgICAgdGhpcy5ieXdlZWtkYXkuc29tZVdlZWtzID0gbnVsbDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHRoaXMuYnl3ZWVrZGF5ID0gbnVsbDtcbiAgICAgICAgfVxuICAgIH1cbiAgICAvKipcbiAgICAgKiBUZXN0IHdoZXRoZXIgdGhlIHJydWxlIGNhbiBiZSBmdWxseSBjb252ZXJ0ZWQgdG8gdGV4dC5cbiAgICAgKlxuICAgICAqIEBwYXJhbSB7UlJ1bGV9IHJydWxlXG4gICAgICogQHJldHVybiB7Qm9vbGVhbn1cbiAgICAgKi9cbiAgICBUb1RleHQuaXNGdWxseUNvbnZlcnRpYmxlID0gZnVuY3Rpb24gKHJydWxlKSB7XG4gICAgICAgIHZhciBjYW5Db252ZXJ0ID0gdHJ1ZTtcbiAgICAgICAgaWYgKCEocnJ1bGUub3B0aW9ucy5mcmVxIGluIFRvVGV4dC5JTVBMRU1FTlRFRCkpXG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIGlmIChycnVsZS5vcmlnT3B0aW9ucy51bnRpbCAmJiBycnVsZS5vcmlnT3B0aW9ucy5jb3VudClcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgZm9yICh2YXIga2V5IGluIHJydWxlLm9yaWdPcHRpb25zKSB7XG4gICAgICAgICAgICBpZiAoY29udGFpbnMoWydkdHN0YXJ0JywgJ3drc3QnLCAnZnJlcSddLCBrZXkpKVxuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgaWYgKCFjb250YWlucyhUb1RleHQuSU1QTEVNRU5URURbcnJ1bGUub3B0aW9ucy5mcmVxXSwga2V5KSlcbiAgICAgICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIGNhbkNvbnZlcnQ7XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLmlzRnVsbHlDb252ZXJ0aWJsZSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIFRvVGV4dC5pc0Z1bGx5Q29udmVydGlibGUodGhpcy5ycnVsZSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBQZXJmb3JtIHRoZSBjb252ZXJzaW9uLiBPbmx5IHNvbWUgb2YgdGhlIGZyZXF1ZW5jaWVzIGFyZSBzdXBwb3J0ZWQuXG4gICAgICogSWYgc29tZSBvZiB0aGUgcnJ1bGUncyBvcHRpb25zIGFyZW4ndCBzdXBwb3J0ZWQsIHRoZXknbGxcbiAgICAgKiBiZSBvbWl0dGVkIGZyb20gdGhlIG91dHB1dCBhbiBcIih+IGFwcHJveGltYXRlKVwiIHdpbGwgYmUgYXBwZW5kZWQuXG4gICAgICpcbiAgICAgKiBAcmV0dXJuIHsqfVxuICAgICAqL1xuICAgIFRvVGV4dC5wcm90b3R5cGUudG9TdHJpbmcgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBnZXR0ZXh0ID0gdGhpcy5nZXR0ZXh0O1xuICAgICAgICBpZiAoISh0aGlzLm9wdGlvbnMuZnJlcSBpbiBUb1RleHQuSU1QTEVNRU5URUQpKSB7XG4gICAgICAgICAgICByZXR1cm4gZ2V0dGV4dCgnUlJ1bGUgZXJyb3I6IFVuYWJsZSB0byBmdWxseSBjb252ZXJ0IHRoaXMgcnJ1bGUgdG8gdGV4dCcpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMudGV4dCA9IFtnZXR0ZXh0KCdldmVyeScpXTtcbiAgICAgICAgLy8gZXNsaW50LWRpc2FibGUtbmV4dC1saW5lIEB0eXBlc2NyaXB0LWVzbGludC9iYW4tdHMtY29tbWVudFxuICAgICAgICAvLyBAdHMtaWdub3JlXG4gICAgICAgIHRoaXNbUlJ1bGUuRlJFUVVFTkNJRVNbdGhpcy5vcHRpb25zLmZyZXFdXSgpO1xuICAgICAgICBpZiAodGhpcy5vcHRpb25zLnVudGlsKSB7XG4gICAgICAgICAgICB0aGlzLmFkZChnZXR0ZXh0KCd1bnRpbCcpKTtcbiAgICAgICAgICAgIHZhciB1bnRpbCA9IHRoaXMub3B0aW9ucy51bnRpbDtcbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMuZGF0ZUZvcm1hdHRlcih1bnRpbC5nZXRVVENGdWxsWWVhcigpLCB0aGlzLmxhbmd1YWdlLm1vbnRoTmFtZXNbdW50aWwuZ2V0VVRDTW9udGgoKV0sIHVudGlsLmdldFVUQ0RhdGUoKSkpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHRoaXMub3B0aW9ucy5jb3VudCkge1xuICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnZm9yJykpXG4gICAgICAgICAgICAgICAgLmFkZCh0aGlzLm9wdGlvbnMuY291bnQudG9TdHJpbmcoKSlcbiAgICAgICAgICAgICAgICAuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5jb3VudCkgPyBnZXR0ZXh0KCd0aW1lcycpIDogZ2V0dGV4dCgndGltZScpKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoIXRoaXMuaXNGdWxseUNvbnZlcnRpYmxlKCkpXG4gICAgICAgICAgICB0aGlzLmFkZChnZXR0ZXh0KCcofiBhcHByb3hpbWF0ZSknKSk7XG4gICAgICAgIHJldHVybiB0aGlzLnRleHQuam9pbignJyk7XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLkhPVVJMWSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIGdldHRleHQgPSB0aGlzLmdldHRleHQ7XG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwgIT09IDEpXG4gICAgICAgICAgICB0aGlzLmFkZCh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwudG9TdHJpbmcoKSk7XG4gICAgICAgIHRoaXMuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5pbnRlcnZhbCkgPyBnZXR0ZXh0KCdob3VycycpIDogZ2V0dGV4dCgnaG91cicpKTtcbiAgICB9O1xuICAgIFRvVGV4dC5wcm90b3R5cGUuTUlOVVRFTFkgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBnZXR0ZXh0ID0gdGhpcy5nZXR0ZXh0O1xuICAgICAgICBpZiAodGhpcy5vcHRpb25zLmludGVydmFsICE9PSAxKVxuICAgICAgICAgICAgdGhpcy5hZGQodGhpcy5vcHRpb25zLmludGVydmFsLnRvU3RyaW5nKCkpO1xuICAgICAgICB0aGlzLmFkZCh0aGlzLnBsdXJhbCh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwpXG4gICAgICAgICAgICA/IGdldHRleHQoJ21pbnV0ZXMnKVxuICAgICAgICAgICAgOiBnZXR0ZXh0KCdtaW51dGUnKSk7XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLkRBSUxZID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgZ2V0dGV4dCA9IHRoaXMuZ2V0dGV4dDtcbiAgICAgICAgaWYgKHRoaXMub3B0aW9ucy5pbnRlcnZhbCAhPT0gMSlcbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMub3B0aW9ucy5pbnRlcnZhbC50b1N0cmluZygpKTtcbiAgICAgICAgaWYgKHRoaXMuYnl3ZWVrZGF5ICYmIHRoaXMuYnl3ZWVrZGF5LmlzV2Vla2RheXMpIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5pbnRlcnZhbClcbiAgICAgICAgICAgICAgICA/IGdldHRleHQoJ3dlZWtkYXlzJylcbiAgICAgICAgICAgICAgICA6IGdldHRleHQoJ3dlZWtkYXknKSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICB0aGlzLmFkZCh0aGlzLnBsdXJhbCh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwpID8gZ2V0dGV4dCgnZGF5cycpIDogZ2V0dGV4dCgnZGF5JykpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLm9yaWdPcHRpb25zLmJ5bW9udGgpIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ2luJykpO1xuICAgICAgICAgICAgdGhpcy5fYnltb250aCgpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLmJ5bW9udGhkYXkpIHtcbiAgICAgICAgICAgIHRoaXMuX2J5bW9udGhkYXkoKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0aGlzLmJ5d2Vla2RheSkge1xuICAgICAgICAgICAgdGhpcy5fYnl3ZWVrZGF5KCk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAodGhpcy5vcmlnT3B0aW9ucy5ieWhvdXIpIHtcbiAgICAgICAgICAgIHRoaXMuX2J5aG91cigpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLldFRUtMWSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIGdldHRleHQgPSB0aGlzLmdldHRleHQ7XG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwgIT09IDEpIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMub3B0aW9ucy5pbnRlcnZhbC50b1N0cmluZygpKS5hZGQodGhpcy5wbHVyYWwodGhpcy5vcHRpb25zLmludGVydmFsKSA/IGdldHRleHQoJ3dlZWtzJykgOiBnZXR0ZXh0KCd3ZWVrJykpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLmJ5d2Vla2RheSAmJiB0aGlzLmJ5d2Vla2RheS5pc1dlZWtkYXlzKSB7XG4gICAgICAgICAgICBpZiAodGhpcy5vcHRpb25zLmludGVydmFsID09PSAxKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5hZGQodGhpcy5wbHVyYWwodGhpcy5vcHRpb25zLmludGVydmFsKVxuICAgICAgICAgICAgICAgICAgICA/IGdldHRleHQoJ3dlZWtkYXlzJylcbiAgICAgICAgICAgICAgICAgICAgOiBnZXR0ZXh0KCd3ZWVrZGF5JykpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnb24nKSkuYWRkKGdldHRleHQoJ3dlZWtkYXlzJykpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHRoaXMuYnl3ZWVrZGF5ICYmIHRoaXMuYnl3ZWVrZGF5LmlzRXZlcnlEYXkpIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5pbnRlcnZhbCkgPyBnZXR0ZXh0KCdkYXlzJykgOiBnZXR0ZXh0KCdkYXknKSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICBpZiAodGhpcy5vcHRpb25zLmludGVydmFsID09PSAxKVxuICAgICAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ3dlZWsnKSk7XG4gICAgICAgICAgICBpZiAodGhpcy5vcmlnT3B0aW9ucy5ieW1vbnRoKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnaW4nKSk7XG4gICAgICAgICAgICAgICAgdGhpcy5fYnltb250aCgpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKHRoaXMuYnltb250aGRheSkge1xuICAgICAgICAgICAgICAgIHRoaXMuX2J5bW9udGhkYXkoKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2UgaWYgKHRoaXMuYnl3ZWVrZGF5KSB7XG4gICAgICAgICAgICAgICAgdGhpcy5fYnl3ZWVrZGF5KCk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRvVGV4dC5wcm90b3R5cGUuTU9OVEhMWSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIGdldHRleHQgPSB0aGlzLmdldHRleHQ7XG4gICAgICAgIGlmICh0aGlzLm9yaWdPcHRpb25zLmJ5bW9udGgpIHtcbiAgICAgICAgICAgIGlmICh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwgIT09IDEpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmFkZCh0aGlzLm9wdGlvbnMuaW50ZXJ2YWwudG9TdHJpbmcoKSkuYWRkKGdldHRleHQoJ21vbnRocycpKTtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5wbHVyYWwodGhpcy5vcHRpb25zLmludGVydmFsKSlcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnaW4nKSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAvLyB0aGlzLmFkZChnZXR0ZXh0KCdNT05USCcpKVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdGhpcy5fYnltb250aCgpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaWYgKHRoaXMub3B0aW9ucy5pbnRlcnZhbCAhPT0gMSkge1xuICAgICAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMub3B0aW9ucy5pbnRlcnZhbC50b1N0cmluZygpKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5pbnRlcnZhbClcbiAgICAgICAgICAgICAgICA/IGdldHRleHQoJ21vbnRocycpXG4gICAgICAgICAgICAgICAgOiBnZXR0ZXh0KCdtb250aCcpKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAodGhpcy5ieW1vbnRoZGF5KSB7XG4gICAgICAgICAgICB0aGlzLl9ieW1vbnRoZGF5KCk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAodGhpcy5ieXdlZWtkYXkgJiYgdGhpcy5ieXdlZWtkYXkuaXNXZWVrZGF5cykge1xuICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnb24nKSkuYWRkKGdldHRleHQoJ3dlZWtkYXlzJykpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHRoaXMuYnl3ZWVrZGF5KSB7XG4gICAgICAgICAgICB0aGlzLl9ieXdlZWtkYXkoKTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgVG9UZXh0LnByb3RvdHlwZS5ZRUFSTFkgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBnZXR0ZXh0ID0gdGhpcy5nZXR0ZXh0O1xuICAgICAgICBpZiAodGhpcy5vcmlnT3B0aW9ucy5ieW1vbnRoKSB7XG4gICAgICAgICAgICBpZiAodGhpcy5vcHRpb25zLmludGVydmFsICE9PSAxKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5hZGQodGhpcy5vcHRpb25zLmludGVydmFsLnRvU3RyaW5nKCkpO1xuICAgICAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ3llYXJzJykpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgLy8gdGhpcy5hZGQoZ2V0dGV4dCgnWUVBUicpKVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdGhpcy5fYnltb250aCgpO1xuICAgICAgICB9XG4gICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgaWYgKHRoaXMub3B0aW9ucy5pbnRlcnZhbCAhPT0gMSkge1xuICAgICAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMub3B0aW9ucy5pbnRlcnZhbC50b1N0cmluZygpKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHRoaXMuYWRkKHRoaXMucGx1cmFsKHRoaXMub3B0aW9ucy5pbnRlcnZhbCkgPyBnZXR0ZXh0KCd5ZWFycycpIDogZ2V0dGV4dCgneWVhcicpKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAodGhpcy5ieW1vbnRoZGF5KSB7XG4gICAgICAgICAgICB0aGlzLl9ieW1vbnRoZGF5KCk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAodGhpcy5ieXdlZWtkYXkpIHtcbiAgICAgICAgICAgIHRoaXMuX2J5d2Vla2RheSgpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMuYnl5ZWFyZGF5KSB7XG4gICAgICAgICAgICB0aGlzLmFkZChnZXR0ZXh0KCdvbiB0aGUnKSlcbiAgICAgICAgICAgICAgICAuYWRkKHRoaXMubGlzdCh0aGlzLm9wdGlvbnMuYnl5ZWFyZGF5LCB0aGlzLm50aCwgZ2V0dGV4dCgnYW5kJykpKVxuICAgICAgICAgICAgICAgIC5hZGQoZ2V0dGV4dCgnZGF5JykpO1xuICAgICAgICB9XG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMuYnl3ZWVrbm8pIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ2luJykpXG4gICAgICAgICAgICAgICAgLmFkZCh0aGlzLnBsdXJhbCh0aGlzLm9wdGlvbnMuYnl3ZWVrbm8ubGVuZ3RoKVxuICAgICAgICAgICAgICAgID8gZ2V0dGV4dCgnd2Vla3MnKVxuICAgICAgICAgICAgICAgIDogZ2V0dGV4dCgnd2VlaycpKVxuICAgICAgICAgICAgICAgIC5hZGQodGhpcy5saXN0KHRoaXMub3B0aW9ucy5ieXdlZWtubywgdW5kZWZpbmVkLCBnZXR0ZXh0KCdhbmQnKSkpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLl9ieW1vbnRoZGF5ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgZ2V0dGV4dCA9IHRoaXMuZ2V0dGV4dDtcbiAgICAgICAgaWYgKHRoaXMuYnl3ZWVrZGF5ICYmIHRoaXMuYnl3ZWVrZGF5LmFsbFdlZWtzKSB7XG4gICAgICAgICAgICB0aGlzLmFkZChnZXR0ZXh0KCdvbicpKVxuICAgICAgICAgICAgICAgIC5hZGQodGhpcy5saXN0KHRoaXMuYnl3ZWVrZGF5LmFsbFdlZWtzLCB0aGlzLndlZWtkYXl0ZXh0LCBnZXR0ZXh0KCdvcicpKSlcbiAgICAgICAgICAgICAgICAuYWRkKGdldHRleHQoJ3RoZScpKVxuICAgICAgICAgICAgICAgIC5hZGQodGhpcy5saXN0KHRoaXMuYnltb250aGRheSwgdGhpcy5udGgsIGdldHRleHQoJ29yJykpKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ29uIHRoZScpKS5hZGQodGhpcy5saXN0KHRoaXMuYnltb250aGRheSwgdGhpcy5udGgsIGdldHRleHQoJ2FuZCcpKSk7XG4gICAgICAgIH1cbiAgICAgICAgLy8gdGhpcy5hZGQoZ2V0dGV4dCgnREFZJykpXG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLl9ieXdlZWtkYXkgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBnZXR0ZXh0ID0gdGhpcy5nZXR0ZXh0O1xuICAgICAgICBpZiAodGhpcy5ieXdlZWtkYXkuYWxsV2Vla3MgJiYgIXRoaXMuYnl3ZWVrZGF5LmlzV2Vla2RheXMpIHtcbiAgICAgICAgICAgIHRoaXMuYWRkKGdldHRleHQoJ29uJykpLmFkZCh0aGlzLmxpc3QodGhpcy5ieXdlZWtkYXkuYWxsV2Vla3MsIHRoaXMud2Vla2RheXRleHQpKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAodGhpcy5ieXdlZWtkYXkuc29tZVdlZWtzKSB7XG4gICAgICAgICAgICBpZiAodGhpcy5ieXdlZWtkYXkuYWxsV2Vla3MpXG4gICAgICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnYW5kJykpO1xuICAgICAgICAgICAgdGhpcy5hZGQoZ2V0dGV4dCgnb24gdGhlJykpLmFkZCh0aGlzLmxpc3QodGhpcy5ieXdlZWtkYXkuc29tZVdlZWtzLCB0aGlzLndlZWtkYXl0ZXh0LCBnZXR0ZXh0KCdhbmQnKSkpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLl9ieWhvdXIgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBnZXR0ZXh0ID0gdGhpcy5nZXR0ZXh0O1xuICAgICAgICB0aGlzLmFkZChnZXR0ZXh0KCdhdCcpKS5hZGQodGhpcy5saXN0KHRoaXMub3JpZ09wdGlvbnMuYnlob3VyLCB1bmRlZmluZWQsIGdldHRleHQoJ2FuZCcpKSk7XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLl9ieW1vbnRoID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB0aGlzLmFkZCh0aGlzLmxpc3QodGhpcy5vcHRpb25zLmJ5bW9udGgsIHRoaXMubW9udGh0ZXh0LCB0aGlzLmdldHRleHQoJ2FuZCcpKSk7XG4gICAgfTtcbiAgICBUb1RleHQucHJvdG90eXBlLm50aCA9IGZ1bmN0aW9uIChuKSB7XG4gICAgICAgIG4gPSBwYXJzZUludChuLnRvU3RyaW5nKCksIDEwKTtcbiAgICAgICAgdmFyIG50aDtcbiAgICAgICAgdmFyIGdldHRleHQgPSB0aGlzLmdldHRleHQ7XG4gICAgICAgIGlmIChuID09PSAtMSlcbiAgICAgICAgICAgIHJldHVybiBnZXR0ZXh0KCdsYXN0Jyk7XG4gICAgICAgIHZhciBucG9zID0gTWF0aC5hYnMobik7XG4gICAgICAgIHN3aXRjaCAobnBvcykge1xuICAgICAgICAgICAgY2FzZSAxOlxuICAgICAgICAgICAgY2FzZSAyMTpcbiAgICAgICAgICAgIGNhc2UgMzE6XG4gICAgICAgICAgICAgICAgbnRoID0gbnBvcyArIGdldHRleHQoJ3N0Jyk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlIDI6XG4gICAgICAgICAgICBjYXNlIDIyOlxuICAgICAgICAgICAgICAgIG50aCA9IG5wb3MgKyBnZXR0ZXh0KCduZCcpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAzOlxuICAgICAgICAgICAgY2FzZSAyMzpcbiAgICAgICAgICAgICAgICBudGggPSBucG9zICsgZ2V0dGV4dCgncmQnKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgbnRoID0gbnBvcyArIGdldHRleHQoJ3RoJyk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIG4gPCAwID8gbnRoICsgJyAnICsgZ2V0dGV4dCgnbGFzdCcpIDogbnRoO1xuICAgIH07XG4gICAgVG9UZXh0LnByb3RvdHlwZS5tb250aHRleHQgPSBmdW5jdGlvbiAobSkge1xuICAgICAgICByZXR1cm4gdGhpcy5sYW5ndWFnZS5tb250aE5hbWVzW20gLSAxXTtcbiAgICB9O1xuICAgIFRvVGV4dC5wcm90b3R5cGUud2Vla2RheXRleHQgPSBmdW5jdGlvbiAod2RheSkge1xuICAgICAgICB2YXIgd2Vla2RheSA9IGlzTnVtYmVyKHdkYXkpID8gKHdkYXkgKyAxKSAlIDcgOiB3ZGF5LmdldEpzV2Vla2RheSgpO1xuICAgICAgICByZXR1cm4gKCh3ZGF5Lm4gPyB0aGlzLm50aCh3ZGF5Lm4pICsgJyAnIDogJycpICtcbiAgICAgICAgICAgIHRoaXMubGFuZ3VhZ2UuZGF5TmFtZXNbd2Vla2RheV0pO1xuICAgIH07XG4gICAgVG9UZXh0LnByb3RvdHlwZS5wbHVyYWwgPSBmdW5jdGlvbiAobikge1xuICAgICAgICByZXR1cm4gbiAlIDEwMCAhPT0gMTtcbiAgICB9O1xuICAgIFRvVGV4dC5wcm90b3R5cGUuYWRkID0gZnVuY3Rpb24gKHMpIHtcbiAgICAgICAgdGhpcy50ZXh0LnB1c2goJyAnKTtcbiAgICAgICAgdGhpcy50ZXh0LnB1c2gocyk7XG4gICAgICAgIHJldHVybiB0aGlzO1xuICAgIH07XG4gICAgVG9UZXh0LnByb3RvdHlwZS5saXN0ID0gZnVuY3Rpb24gKGFyciwgY2FsbGJhY2ssIGZpbmFsRGVsaW0sIGRlbGltKSB7XG4gICAgICAgIHZhciBfdGhpcyA9IHRoaXM7XG4gICAgICAgIGlmIChkZWxpbSA9PT0gdm9pZCAwKSB7IGRlbGltID0gJywnOyB9XG4gICAgICAgIGlmICghaXNBcnJheShhcnIpKSB7XG4gICAgICAgICAgICBhcnIgPSBbYXJyXTtcbiAgICAgICAgfVxuICAgICAgICB2YXIgZGVsaW1Kb2luID0gZnVuY3Rpb24gKGFycmF5LCBkZWxpbWl0ZXIsIGZpbmFsRGVsaW1pdGVyKSB7XG4gICAgICAgICAgICB2YXIgbGlzdCA9ICcnO1xuICAgICAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBhcnJheS5sZW5ndGg7IGkrKykge1xuICAgICAgICAgICAgICAgIGlmIChpICE9PSAwKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChpID09PSBhcnJheS5sZW5ndGggLSAxKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBsaXN0ICs9ICcgJyArIGZpbmFsRGVsaW1pdGVyICsgJyAnO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgbGlzdCArPSBkZWxpbWl0ZXIgKyAnICc7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgbGlzdCArPSBhcnJheVtpXTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiBsaXN0O1xuICAgICAgICB9O1xuICAgICAgICBjYWxsYmFjayA9XG4gICAgICAgICAgICBjYWxsYmFjayB8fFxuICAgICAgICAgICAgICAgIGZ1bmN0aW9uIChvKSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBvLnRvU3RyaW5nKCk7XG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgdmFyIHJlYWxDYWxsYmFjayA9IGZ1bmN0aW9uIChhcmcpIHtcbiAgICAgICAgICAgIHJldHVybiBjYWxsYmFjayAmJiBjYWxsYmFjay5jYWxsKF90aGlzLCBhcmcpO1xuICAgICAgICB9O1xuICAgICAgICBpZiAoZmluYWxEZWxpbSkge1xuICAgICAgICAgICAgcmV0dXJuIGRlbGltSm9pbihhcnIubWFwKHJlYWxDYWxsYmFjayksIGRlbGltLCBmaW5hbERlbGltKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIHJldHVybiBhcnIubWFwKHJlYWxDYWxsYmFjaykuam9pbihkZWxpbSArICcgJyk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIHJldHVybiBUb1RleHQ7XG59KCkpO1xuZXhwb3J0IGRlZmF1bHQgVG9UZXh0O1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9dG90ZXh0LmpzLm1hcCIsImltcG9ydCB7IFJSdWxlLCBERUZBVUxUX09QVElPTlMgfSBmcm9tICcuL3JydWxlJztcbmltcG9ydCB7IGluY2x1ZGVzLCBpc1ByZXNlbnQsIGlzQXJyYXksIGlzTnVtYmVyLCB0b0FycmF5IH0gZnJvbSAnLi9oZWxwZXJzJztcbmltcG9ydCB7IFdlZWtkYXkgfSBmcm9tICcuL3dlZWtkYXknO1xuaW1wb3J0IGRhdGV1dGlsIGZyb20gJy4vZGF0ZXV0aWwnO1xuaW1wb3J0IHsgRGF0ZVdpdGhab25lIH0gZnJvbSAnLi9kYXRld2l0aHpvbmUnO1xuZXhwb3J0IGZ1bmN0aW9uIG9wdGlvbnNUb1N0cmluZyhvcHRpb25zKSB7XG4gICAgdmFyIHJydWxlID0gW107XG4gICAgdmFyIGR0c3RhcnQgPSAnJztcbiAgICB2YXIga2V5cyA9IE9iamVjdC5rZXlzKG9wdGlvbnMpO1xuICAgIHZhciBkZWZhdWx0S2V5cyA9IE9iamVjdC5rZXlzKERFRkFVTFRfT1BUSU9OUyk7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBrZXlzLmxlbmd0aDsgaSsrKSB7XG4gICAgICAgIGlmIChrZXlzW2ldID09PSAndHppZCcpXG4gICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgaWYgKCFpbmNsdWRlcyhkZWZhdWx0S2V5cywga2V5c1tpXSkpXG4gICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgdmFyIGtleSA9IGtleXNbaV0udG9VcHBlckNhc2UoKTtcbiAgICAgICAgdmFyIHZhbHVlID0gb3B0aW9uc1trZXlzW2ldXTtcbiAgICAgICAgdmFyIG91dFZhbHVlID0gJyc7XG4gICAgICAgIGlmICghaXNQcmVzZW50KHZhbHVlKSB8fCAoaXNBcnJheSh2YWx1ZSkgJiYgIXZhbHVlLmxlbmd0aCkpXG4gICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgc3dpdGNoIChrZXkpIHtcbiAgICAgICAgICAgIGNhc2UgJ0ZSRVEnOlxuICAgICAgICAgICAgICAgIG91dFZhbHVlID0gUlJ1bGUuRlJFUVVFTkNJRVNbb3B0aW9ucy5mcmVxXTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgJ1dLU1QnOlxuICAgICAgICAgICAgICAgIGlmIChpc051bWJlcih2YWx1ZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgb3V0VmFsdWUgPSBuZXcgV2Vla2RheSh2YWx1ZSkudG9TdHJpbmcoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIG91dFZhbHVlID0gdmFsdWUudG9TdHJpbmcoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdCWVdFRUtEQVknOlxuICAgICAgICAgICAgICAgIC8qXG4gICAgICAgICAgICAgICAgICBOT1RFOiBCWVdFRUtEQVkgaXMgYSBzcGVjaWFsIGNhc2UuXG4gICAgICAgICAgICAgICAgICBSUnVsZSgpIGRlY29uc3RydWN0cyB0aGUgcnVsZS5vcHRpb25zLmJ5d2Vla2RheSBhcnJheVxuICAgICAgICAgICAgICAgICAgaW50byBhbiBhcnJheSBvZiBXZWVrZGF5IGFyZ3VtZW50cy5cbiAgICAgICAgICAgICAgICAgIE9uIHRoZSBvdGhlciBoYW5kLCBydWxlLm9yaWdPcHRpb25zIGlzIGFuIGFycmF5IG9mIFdlZWtkYXlzLlxuICAgICAgICAgICAgICAgICAgV2UgbmVlZCB0byBoYW5kbGUgYm90aCBjYXNlcyBoZXJlLlxuICAgICAgICAgICAgICAgICAgSXQgbWlnaHQgYmUgd29ydGggY2hhbmdlIFJSdWxlIHRvIGtlZXAgdGhlIFdlZWtkYXlzLlxuICAgICAgICBcbiAgICAgICAgICAgICAgICAgIEFsc28sIEJZV0VFS0RBWSAodXNlZCBieSBSUnVsZSkgdnMuIEJZREFZIChSRkMpXG4gICAgICAgIFxuICAgICAgICAgICAgICAgICAgKi9cbiAgICAgICAgICAgICAgICBrZXkgPSAnQllEQVknO1xuICAgICAgICAgICAgICAgIG91dFZhbHVlID0gdG9BcnJheSh2YWx1ZSlcbiAgICAgICAgICAgICAgICAgICAgLm1hcChmdW5jdGlvbiAod2RheSkge1xuICAgICAgICAgICAgICAgICAgICBpZiAod2RheSBpbnN0YW5jZW9mIFdlZWtkYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiB3ZGF5O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChpc0FycmF5KHdkYXkpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gbmV3IFdlZWtkYXkod2RheVswXSwgd2RheVsxXSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIG5ldyBXZWVrZGF5KHdkYXkpO1xuICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgICAgIC50b1N0cmluZygpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnRFRTVEFSVCc6XG4gICAgICAgICAgICAgICAgZHRzdGFydCA9IGJ1aWxkRHRzdGFydCh2YWx1ZSwgb3B0aW9ucy50emlkKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgJ1VOVElMJzpcbiAgICAgICAgICAgICAgICBvdXRWYWx1ZSA9IGRhdGV1dGlsLnRpbWVUb1VudGlsU3RyaW5nKHZhbHVlLCAhb3B0aW9ucy50emlkKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgaWYgKGlzQXJyYXkodmFsdWUpKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBzdHJWYWx1ZXMgPSBbXTtcbiAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgaiA9IDA7IGogPCB2YWx1ZS5sZW5ndGg7IGorKykge1xuICAgICAgICAgICAgICAgICAgICAgICAgc3RyVmFsdWVzW2pdID0gU3RyaW5nKHZhbHVlW2pdKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBvdXRWYWx1ZSA9IHN0clZhbHVlcy50b1N0cmluZygpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgb3V0VmFsdWUgPSBTdHJpbmcodmFsdWUpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBpZiAob3V0VmFsdWUpIHtcbiAgICAgICAgICAgIHJydWxlLnB1c2goW2tleSwgb3V0VmFsdWVdKTtcbiAgICAgICAgfVxuICAgIH1cbiAgICB2YXIgcnVsZXMgPSBycnVsZVxuICAgICAgICAubWFwKGZ1bmN0aW9uIChfYSkge1xuICAgICAgICB2YXIga2V5ID0gX2FbMF0sIHZhbHVlID0gX2FbMV07XG4gICAgICAgIHJldHVybiBcIlwiLmNvbmNhdChrZXksIFwiPVwiKS5jb25jYXQodmFsdWUudG9TdHJpbmcoKSk7XG4gICAgfSlcbiAgICAgICAgLmpvaW4oJzsnKTtcbiAgICB2YXIgcnVsZVN0cmluZyA9ICcnO1xuICAgIGlmIChydWxlcyAhPT0gJycpIHtcbiAgICAgICAgcnVsZVN0cmluZyA9IFwiUlJVTEU6XCIuY29uY2F0KHJ1bGVzKTtcbiAgICB9XG4gICAgcmV0dXJuIFtkdHN0YXJ0LCBydWxlU3RyaW5nXS5maWx0ZXIoZnVuY3Rpb24gKHgpIHsgcmV0dXJuICEheDsgfSkuam9pbignXFxuJyk7XG59XG5mdW5jdGlvbiBidWlsZER0c3RhcnQoZHRzdGFydCwgdHppZCkge1xuICAgIGlmICghZHRzdGFydCkge1xuICAgICAgICByZXR1cm4gJyc7XG4gICAgfVxuICAgIHJldHVybiAnRFRTVEFSVCcgKyBuZXcgRGF0ZVdpdGhab25lKG5ldyBEYXRlKGR0c3RhcnQpLCB0emlkKS50b1N0cmluZygpO1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9b3B0aW9uc3Rvc3RyaW5nLmpzLm1hcCIsImltcG9ydCB7IF9fYXNzaWduIH0gZnJvbSBcInRzbGliXCI7XG5pbXBvcnQgeyBmcmVxSXNEYWlseU9yR3JlYXRlciB9IGZyb20gJy4vdHlwZXMnO1xuaW1wb3J0IHsgaW5jbHVkZXMsIG5vdEVtcHR5LCBpc1ByZXNlbnQsIGlzTnVtYmVyLCBpc0FycmF5LCBpc1dlZWtkYXlTdHIsIH0gZnJvbSAnLi9oZWxwZXJzJztcbmltcG9ydCB7IFJSdWxlLCBkZWZhdWx0S2V5cywgREVGQVVMVF9PUFRJT05TIH0gZnJvbSAnLi9ycnVsZSc7XG5pbXBvcnQgZGF0ZXV0aWwgZnJvbSAnLi9kYXRldXRpbCc7XG5pbXBvcnQgeyBXZWVrZGF5IH0gZnJvbSAnLi93ZWVrZGF5JztcbmltcG9ydCB7IFRpbWUgfSBmcm9tICcuL2RhdGV0aW1lJztcbmV4cG9ydCBmdW5jdGlvbiBpbml0aWFsaXplT3B0aW9ucyhvcHRpb25zKSB7XG4gICAgdmFyIGludmFsaWQgPSBbXTtcbiAgICB2YXIga2V5cyA9IE9iamVjdC5rZXlzKG9wdGlvbnMpO1xuICAgIC8vIFNoYWxsb3cgY29weSBmb3Igb3B0aW9ucyBhbmQgb3JpZ09wdGlvbnMgYW5kIGNoZWNrIGZvciBpbnZhbGlkXG4gICAgZm9yICh2YXIgX2kgPSAwLCBrZXlzXzEgPSBrZXlzOyBfaSA8IGtleXNfMS5sZW5ndGg7IF9pKyspIHtcbiAgICAgICAgdmFyIGtleSA9IGtleXNfMVtfaV07XG4gICAgICAgIGlmICghaW5jbHVkZXMoZGVmYXVsdEtleXMsIGtleSkpXG4gICAgICAgICAgICBpbnZhbGlkLnB1c2goa2V5KTtcbiAgICAgICAgaWYgKGRhdGV1dGlsLmlzRGF0ZShvcHRpb25zW2tleV0pICYmICFkYXRldXRpbC5pc1ZhbGlkRGF0ZShvcHRpb25zW2tleV0pKSB7XG4gICAgICAgICAgICBpbnZhbGlkLnB1c2goa2V5KTtcbiAgICAgICAgfVxuICAgIH1cbiAgICBpZiAoaW52YWxpZC5sZW5ndGgpIHtcbiAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdJbnZhbGlkIG9wdGlvbnM6ICcgKyBpbnZhbGlkLmpvaW4oJywgJykpO1xuICAgIH1cbiAgICByZXR1cm4gX19hc3NpZ24oe30sIG9wdGlvbnMpO1xufVxuZXhwb3J0IGZ1bmN0aW9uIHBhcnNlT3B0aW9ucyhvcHRpb25zKSB7XG4gICAgdmFyIG9wdHMgPSBfX2Fzc2lnbihfX2Fzc2lnbih7fSwgREVGQVVMVF9PUFRJT05TKSwgaW5pdGlhbGl6ZU9wdGlvbnMob3B0aW9ucykpO1xuICAgIGlmIChpc1ByZXNlbnQob3B0cy5ieWVhc3RlcikpXG4gICAgICAgIG9wdHMuZnJlcSA9IFJSdWxlLllFQVJMWTtcbiAgICBpZiAoIShpc1ByZXNlbnQob3B0cy5mcmVxKSAmJiBSUnVsZS5GUkVRVUVOQ0lFU1tvcHRzLmZyZXFdKSkge1xuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJJbnZhbGlkIGZyZXF1ZW5jeTogXCIuY29uY2F0KG9wdHMuZnJlcSwgXCIgXCIpLmNvbmNhdChvcHRpb25zLmZyZXEpKTtcbiAgICB9XG4gICAgaWYgKCFvcHRzLmR0c3RhcnQpXG4gICAgICAgIG9wdHMuZHRzdGFydCA9IG5ldyBEYXRlKG5ldyBEYXRlKCkuc2V0TWlsbGlzZWNvbmRzKDApKTtcbiAgICBpZiAoIWlzUHJlc2VudChvcHRzLndrc3QpKSB7XG4gICAgICAgIG9wdHMud2tzdCA9IFJSdWxlLk1PLndlZWtkYXk7XG4gICAgfVxuICAgIGVsc2UgaWYgKGlzTnVtYmVyKG9wdHMud2tzdCkpIHtcbiAgICAgICAgLy8gY29vbCwganVzdCBrZWVwIGl0IGxpa2UgdGhhdFxuICAgIH1cbiAgICBlbHNlIHtcbiAgICAgICAgb3B0cy53a3N0ID0gb3B0cy53a3N0LndlZWtkYXk7XG4gICAgfVxuICAgIGlmIChpc1ByZXNlbnQob3B0cy5ieXNldHBvcykpIHtcbiAgICAgICAgaWYgKGlzTnVtYmVyKG9wdHMuYnlzZXRwb3MpKVxuICAgICAgICAgICAgb3B0cy5ieXNldHBvcyA9IFtvcHRzLmJ5c2V0cG9zXTtcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBvcHRzLmJ5c2V0cG9zLmxlbmd0aDsgaSsrKSB7XG4gICAgICAgICAgICB2YXIgdiA9IG9wdHMuYnlzZXRwb3NbaV07XG4gICAgICAgICAgICBpZiAodiA9PT0gMCB8fCAhKHYgPj0gLTM2NiAmJiB2IDw9IDM2NikpIHtcbiAgICAgICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ2J5c2V0cG9zIG11c3QgYmUgYmV0d2VlbiAxIGFuZCAzNjYsJyArICcgb3IgYmV0d2VlbiAtMzY2IGFuZCAtMScpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfVxuICAgIGlmICghKEJvb2xlYW4ob3B0cy5ieXdlZWtubykgfHxcbiAgICAgICAgbm90RW1wdHkob3B0cy5ieXdlZWtubykgfHxcbiAgICAgICAgbm90RW1wdHkob3B0cy5ieXllYXJkYXkpIHx8XG4gICAgICAgIEJvb2xlYW4ob3B0cy5ieW1vbnRoZGF5KSB8fFxuICAgICAgICBub3RFbXB0eShvcHRzLmJ5bW9udGhkYXkpIHx8XG4gICAgICAgIGlzUHJlc2VudChvcHRzLmJ5d2Vla2RheSkgfHxcbiAgICAgICAgaXNQcmVzZW50KG9wdHMuYnllYXN0ZXIpKSkge1xuICAgICAgICBzd2l0Y2ggKG9wdHMuZnJlcSkge1xuICAgICAgICAgICAgY2FzZSBSUnVsZS5ZRUFSTFk6XG4gICAgICAgICAgICAgICAgaWYgKCFvcHRzLmJ5bW9udGgpXG4gICAgICAgICAgICAgICAgICAgIG9wdHMuYnltb250aCA9IG9wdHMuZHRzdGFydC5nZXRVVENNb250aCgpICsgMTtcbiAgICAgICAgICAgICAgICBvcHRzLmJ5bW9udGhkYXkgPSBvcHRzLmR0c3RhcnQuZ2V0VVRDRGF0ZSgpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSBSUnVsZS5NT05USExZOlxuICAgICAgICAgICAgICAgIG9wdHMuYnltb250aGRheSA9IG9wdHMuZHRzdGFydC5nZXRVVENEYXRlKCk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlIFJSdWxlLldFRUtMWTpcbiAgICAgICAgICAgICAgICBvcHRzLmJ5d2Vla2RheSA9IFtkYXRldXRpbC5nZXRXZWVrZGF5KG9wdHMuZHRzdGFydCldO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICB9XG4gICAgfVxuICAgIC8vIGJ5bW9udGhcbiAgICBpZiAoaXNQcmVzZW50KG9wdHMuYnltb250aCkgJiYgIWlzQXJyYXkob3B0cy5ieW1vbnRoKSkge1xuICAgICAgICBvcHRzLmJ5bW9udGggPSBbb3B0cy5ieW1vbnRoXTtcbiAgICB9XG4gICAgLy8gYnl5ZWFyZGF5XG4gICAgaWYgKGlzUHJlc2VudChvcHRzLmJ5eWVhcmRheSkgJiZcbiAgICAgICAgIWlzQXJyYXkob3B0cy5ieXllYXJkYXkpICYmXG4gICAgICAgIGlzTnVtYmVyKG9wdHMuYnl5ZWFyZGF5KSkge1xuICAgICAgICBvcHRzLmJ5eWVhcmRheSA9IFtvcHRzLmJ5eWVhcmRheV07XG4gICAgfVxuICAgIC8vIGJ5bW9udGhkYXlcbiAgICBpZiAoIWlzUHJlc2VudChvcHRzLmJ5bW9udGhkYXkpKSB7XG4gICAgICAgIG9wdHMuYnltb250aGRheSA9IFtdO1xuICAgICAgICBvcHRzLmJ5bm1vbnRoZGF5ID0gW107XG4gICAgfVxuICAgIGVsc2UgaWYgKGlzQXJyYXkob3B0cy5ieW1vbnRoZGF5KSkge1xuICAgICAgICB2YXIgYnltb250aGRheSA9IFtdO1xuICAgICAgICB2YXIgYnlubW9udGhkYXkgPSBbXTtcbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBvcHRzLmJ5bW9udGhkYXkubGVuZ3RoOyBpKyspIHtcbiAgICAgICAgICAgIHZhciB2ID0gb3B0cy5ieW1vbnRoZGF5W2ldO1xuICAgICAgICAgICAgaWYgKHYgPiAwKSB7XG4gICAgICAgICAgICAgICAgYnltb250aGRheS5wdXNoKHYpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAodiA8IDApIHtcbiAgICAgICAgICAgICAgICBieW5tb250aGRheS5wdXNoKHYpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIG9wdHMuYnltb250aGRheSA9IGJ5bW9udGhkYXk7XG4gICAgICAgIG9wdHMuYnlubW9udGhkYXkgPSBieW5tb250aGRheTtcbiAgICB9XG4gICAgZWxzZSBpZiAob3B0cy5ieW1vbnRoZGF5IDwgMCkge1xuICAgICAgICBvcHRzLmJ5bm1vbnRoZGF5ID0gW29wdHMuYnltb250aGRheV07XG4gICAgICAgIG9wdHMuYnltb250aGRheSA9IFtdO1xuICAgIH1cbiAgICBlbHNlIHtcbiAgICAgICAgb3B0cy5ieW5tb250aGRheSA9IFtdO1xuICAgICAgICBvcHRzLmJ5bW9udGhkYXkgPSBbb3B0cy5ieW1vbnRoZGF5XTtcbiAgICB9XG4gICAgLy8gYnl3ZWVrbm9cbiAgICBpZiAoaXNQcmVzZW50KG9wdHMuYnl3ZWVrbm8pICYmICFpc0FycmF5KG9wdHMuYnl3ZWVrbm8pKSB7XG4gICAgICAgIG9wdHMuYnl3ZWVrbm8gPSBbb3B0cy5ieXdlZWtub107XG4gICAgfVxuICAgIC8vIGJ5d2Vla2RheSAvIGJ5bndlZWtkYXlcbiAgICBpZiAoIWlzUHJlc2VudChvcHRzLmJ5d2Vla2RheSkpIHtcbiAgICAgICAgb3B0cy5ieW53ZWVrZGF5ID0gbnVsbDtcbiAgICB9XG4gICAgZWxzZSBpZiAoaXNOdW1iZXIob3B0cy5ieXdlZWtkYXkpKSB7XG4gICAgICAgIG9wdHMuYnl3ZWVrZGF5ID0gW29wdHMuYnl3ZWVrZGF5XTtcbiAgICAgICAgb3B0cy5ieW53ZWVrZGF5ID0gbnVsbDtcbiAgICB9XG4gICAgZWxzZSBpZiAoaXNXZWVrZGF5U3RyKG9wdHMuYnl3ZWVrZGF5KSkge1xuICAgICAgICBvcHRzLmJ5d2Vla2RheSA9IFtXZWVrZGF5LmZyb21TdHIob3B0cy5ieXdlZWtkYXkpLndlZWtkYXldO1xuICAgICAgICBvcHRzLmJ5bndlZWtkYXkgPSBudWxsO1xuICAgIH1cbiAgICBlbHNlIGlmIChvcHRzLmJ5d2Vla2RheSBpbnN0YW5jZW9mIFdlZWtkYXkpIHtcbiAgICAgICAgaWYgKCFvcHRzLmJ5d2Vla2RheS5uIHx8IG9wdHMuZnJlcSA+IFJSdWxlLk1PTlRITFkpIHtcbiAgICAgICAgICAgIG9wdHMuYnl3ZWVrZGF5ID0gW29wdHMuYnl3ZWVrZGF5LndlZWtkYXldO1xuICAgICAgICAgICAgb3B0cy5ieW53ZWVrZGF5ID0gbnVsbDtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIG9wdHMuYnlud2Vla2RheSA9IFtbb3B0cy5ieXdlZWtkYXkud2Vla2RheSwgb3B0cy5ieXdlZWtkYXkubl1dO1xuICAgICAgICAgICAgb3B0cy5ieXdlZWtkYXkgPSBudWxsO1xuICAgICAgICB9XG4gICAgfVxuICAgIGVsc2Uge1xuICAgICAgICB2YXIgYnl3ZWVrZGF5ID0gW107XG4gICAgICAgIHZhciBieW53ZWVrZGF5ID0gW107XG4gICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgb3B0cy5ieXdlZWtkYXkubGVuZ3RoOyBpKyspIHtcbiAgICAgICAgICAgIHZhciB3ZGF5ID0gb3B0cy5ieXdlZWtkYXlbaV07XG4gICAgICAgICAgICBpZiAoaXNOdW1iZXIod2RheSkpIHtcbiAgICAgICAgICAgICAgICBieXdlZWtkYXkucHVzaCh3ZGF5KTtcbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2UgaWYgKGlzV2Vla2RheVN0cih3ZGF5KSkge1xuICAgICAgICAgICAgICAgIGJ5d2Vla2RheS5wdXNoKFdlZWtkYXkuZnJvbVN0cih3ZGF5KS53ZWVrZGF5KTtcbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmICghd2RheS5uIHx8IG9wdHMuZnJlcSA+IFJSdWxlLk1PTlRITFkpIHtcbiAgICAgICAgICAgICAgICBieXdlZWtkYXkucHVzaCh3ZGF5LndlZWtkYXkpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgYnlud2Vla2RheS5wdXNoKFt3ZGF5LndlZWtkYXksIHdkYXkubl0pO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIG9wdHMuYnl3ZWVrZGF5ID0gbm90RW1wdHkoYnl3ZWVrZGF5KSA/IGJ5d2Vla2RheSA6IG51bGw7XG4gICAgICAgIG9wdHMuYnlud2Vla2RheSA9IG5vdEVtcHR5KGJ5bndlZWtkYXkpID8gYnlud2Vla2RheSA6IG51bGw7XG4gICAgfVxuICAgIC8vIGJ5aG91clxuICAgIGlmICghaXNQcmVzZW50KG9wdHMuYnlob3VyKSkge1xuICAgICAgICBvcHRzLmJ5aG91ciA9IG9wdHMuZnJlcSA8IFJSdWxlLkhPVVJMWSA/IFtvcHRzLmR0c3RhcnQuZ2V0VVRDSG91cnMoKV0gOiBudWxsO1xuICAgIH1cbiAgICBlbHNlIGlmIChpc051bWJlcihvcHRzLmJ5aG91cikpIHtcbiAgICAgICAgb3B0cy5ieWhvdXIgPSBbb3B0cy5ieWhvdXJdO1xuICAgIH1cbiAgICAvLyBieW1pbnV0ZVxuICAgIGlmICghaXNQcmVzZW50KG9wdHMuYnltaW51dGUpKSB7XG4gICAgICAgIG9wdHMuYnltaW51dGUgPVxuICAgICAgICAgICAgb3B0cy5mcmVxIDwgUlJ1bGUuTUlOVVRFTFkgPyBbb3B0cy5kdHN0YXJ0LmdldFVUQ01pbnV0ZXMoKV0gOiBudWxsO1xuICAgIH1cbiAgICBlbHNlIGlmIChpc051bWJlcihvcHRzLmJ5bWludXRlKSkge1xuICAgICAgICBvcHRzLmJ5bWludXRlID0gW29wdHMuYnltaW51dGVdO1xuICAgIH1cbiAgICAvLyBieXNlY29uZFxuICAgIGlmICghaXNQcmVzZW50KG9wdHMuYnlzZWNvbmQpKSB7XG4gICAgICAgIG9wdHMuYnlzZWNvbmQgPVxuICAgICAgICAgICAgb3B0cy5mcmVxIDwgUlJ1bGUuU0VDT05ETFkgPyBbb3B0cy5kdHN0YXJ0LmdldFVUQ1NlY29uZHMoKV0gOiBudWxsO1xuICAgIH1cbiAgICBlbHNlIGlmIChpc051bWJlcihvcHRzLmJ5c2Vjb25kKSkge1xuICAgICAgICBvcHRzLmJ5c2Vjb25kID0gW29wdHMuYnlzZWNvbmRdO1xuICAgIH1cbiAgICByZXR1cm4geyBwYXJzZWRPcHRpb25zOiBvcHRzIH07XG59XG5leHBvcnQgZnVuY3Rpb24gYnVpbGRUaW1lc2V0KG9wdHMpIHtcbiAgICB2YXIgbWlsbGlzZWNvbmRNb2R1bG8gPSBvcHRzLmR0c3RhcnQuZ2V0VGltZSgpICUgMTAwMDtcbiAgICBpZiAoIWZyZXFJc0RhaWx5T3JHcmVhdGVyKG9wdHMuZnJlcSkpIHtcbiAgICAgICAgcmV0dXJuIFtdO1xuICAgIH1cbiAgICB2YXIgdGltZXNldCA9IFtdO1xuICAgIG9wdHMuYnlob3VyLmZvckVhY2goZnVuY3Rpb24gKGhvdXIpIHtcbiAgICAgICAgb3B0cy5ieW1pbnV0ZS5mb3JFYWNoKGZ1bmN0aW9uIChtaW51dGUpIHtcbiAgICAgICAgICAgIG9wdHMuYnlzZWNvbmQuZm9yRWFjaChmdW5jdGlvbiAoc2Vjb25kKSB7XG4gICAgICAgICAgICAgICAgdGltZXNldC5wdXNoKG5ldyBUaW1lKGhvdXIsIG1pbnV0ZSwgc2Vjb25kLCBtaWxsaXNlY29uZE1vZHVsbykpO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH0pO1xuICAgIHJldHVybiB0aW1lc2V0O1xufVxuLy8jIHNvdXJjZU1hcHBpbmdVUkw9cGFyc2VvcHRpb25zLmpzLm1hcCIsImltcG9ydCB7IF9fYXNzaWduIH0gZnJvbSBcInRzbGliXCI7XG5pbXBvcnQgeyBGcmVxdWVuY3kgfSBmcm9tICcuL3R5cGVzJztcbmltcG9ydCB7IFdlZWtkYXkgfSBmcm9tICcuL3dlZWtkYXknO1xuaW1wb3J0IGRhdGV1dGlsIGZyb20gJy4vZGF0ZXV0aWwnO1xuaW1wb3J0IHsgRGF5cyB9IGZyb20gJy4vcnJ1bGUnO1xuZXhwb3J0IGZ1bmN0aW9uIHBhcnNlU3RyaW5nKHJmY1N0cmluZykge1xuICAgIHZhciBvcHRpb25zID0gcmZjU3RyaW5nXG4gICAgICAgIC5zcGxpdCgnXFxuJylcbiAgICAgICAgLm1hcChwYXJzZUxpbmUpXG4gICAgICAgIC5maWx0ZXIoZnVuY3Rpb24gKHgpIHsgcmV0dXJuIHggIT09IG51bGw7IH0pO1xuICAgIHJldHVybiBfX2Fzc2lnbihfX2Fzc2lnbih7fSwgb3B0aW9uc1swXSksIG9wdGlvbnNbMV0pO1xufVxuZXhwb3J0IGZ1bmN0aW9uIHBhcnNlRHRzdGFydChsaW5lKSB7XG4gICAgdmFyIG9wdGlvbnMgPSB7fTtcbiAgICB2YXIgZHRzdGFydFdpdGhab25lID0gL0RUU1RBUlQoPzo7VFpJRD0oW146PV0rPykpPyg/Ojp8PSkoW147XFxzXSspL2kuZXhlYyhsaW5lKTtcbiAgICBpZiAoIWR0c3RhcnRXaXRoWm9uZSkge1xuICAgICAgICByZXR1cm4gb3B0aW9ucztcbiAgICB9XG4gICAgdmFyIHR6aWQgPSBkdHN0YXJ0V2l0aFpvbmVbMV0sIGR0c3RhcnQgPSBkdHN0YXJ0V2l0aFpvbmVbMl07XG4gICAgaWYgKHR6aWQpIHtcbiAgICAgICAgb3B0aW9ucy50emlkID0gdHppZDtcbiAgICB9XG4gICAgb3B0aW9ucy5kdHN0YXJ0ID0gZGF0ZXV0aWwudW50aWxTdHJpbmdUb0RhdGUoZHRzdGFydCk7XG4gICAgcmV0dXJuIG9wdGlvbnM7XG59XG5mdW5jdGlvbiBwYXJzZUxpbmUocmZjU3RyaW5nKSB7XG4gICAgcmZjU3RyaW5nID0gcmZjU3RyaW5nLnJlcGxhY2UoL15cXHMrfFxccyskLywgJycpO1xuICAgIGlmICghcmZjU3RyaW5nLmxlbmd0aClcbiAgICAgICAgcmV0dXJuIG51bGw7XG4gICAgdmFyIGhlYWRlciA9IC9eKFtBLVpdKz8pWzo7XS8uZXhlYyhyZmNTdHJpbmcudG9VcHBlckNhc2UoKSk7XG4gICAgaWYgKCFoZWFkZXIpIHtcbiAgICAgICAgcmV0dXJuIHBhcnNlUnJ1bGUocmZjU3RyaW5nKTtcbiAgICB9XG4gICAgdmFyIGtleSA9IGhlYWRlclsxXTtcbiAgICBzd2l0Y2ggKGtleS50b1VwcGVyQ2FzZSgpKSB7XG4gICAgICAgIGNhc2UgJ1JSVUxFJzpcbiAgICAgICAgY2FzZSAnRVhSVUxFJzpcbiAgICAgICAgICAgIHJldHVybiBwYXJzZVJydWxlKHJmY1N0cmluZyk7XG4gICAgICAgIGNhc2UgJ0RUU1RBUlQnOlxuICAgICAgICAgICAgcmV0dXJuIHBhcnNlRHRzdGFydChyZmNTdHJpbmcpO1xuICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKFwiVW5zdXBwb3J0ZWQgUkZDIHByb3AgXCIuY29uY2F0KGtleSwgXCIgaW4gXCIpLmNvbmNhdChyZmNTdHJpbmcpKTtcbiAgICB9XG59XG5mdW5jdGlvbiBwYXJzZVJydWxlKGxpbmUpIHtcbiAgICB2YXIgc3RyaXBwZWRMaW5lID0gbGluZS5yZXBsYWNlKC9eUlJVTEU6L2ksICcnKTtcbiAgICB2YXIgb3B0aW9ucyA9IHBhcnNlRHRzdGFydChzdHJpcHBlZExpbmUpO1xuICAgIHZhciBhdHRycyA9IGxpbmUucmVwbGFjZSgvXig/OlJSVUxFfEVYUlVMRSk6L2ksICcnKS5zcGxpdCgnOycpO1xuICAgIGF0dHJzLmZvckVhY2goZnVuY3Rpb24gKGF0dHIpIHtcbiAgICAgICAgdmFyIF9hID0gYXR0ci5zcGxpdCgnPScpLCBrZXkgPSBfYVswXSwgdmFsdWUgPSBfYVsxXTtcbiAgICAgICAgc3dpdGNoIChrZXkudG9VcHBlckNhc2UoKSkge1xuICAgICAgICAgICAgY2FzZSAnRlJFUSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5mcmVxID0gRnJlcXVlbmN5W3ZhbHVlLnRvVXBwZXJDYXNlKCldO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnV0tTVCc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy53a3N0ID0gRGF5c1t2YWx1ZS50b1VwcGVyQ2FzZSgpXTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgJ0NPVU5UJzpcbiAgICAgICAgICAgIGNhc2UgJ0lOVEVSVkFMJzpcbiAgICAgICAgICAgIGNhc2UgJ0JZU0VUUE9TJzpcbiAgICAgICAgICAgIGNhc2UgJ0JZTU9OVEgnOlxuICAgICAgICAgICAgY2FzZSAnQllNT05USERBWSc6XG4gICAgICAgICAgICBjYXNlICdCWVlFQVJEQVknOlxuICAgICAgICAgICAgY2FzZSAnQllXRUVLTk8nOlxuICAgICAgICAgICAgY2FzZSAnQllIT1VSJzpcbiAgICAgICAgICAgIGNhc2UgJ0JZTUlOVVRFJzpcbiAgICAgICAgICAgIGNhc2UgJ0JZU0VDT05EJzpcbiAgICAgICAgICAgICAgICB2YXIgbnVtID0gcGFyc2VOdW1iZXIodmFsdWUpO1xuICAgICAgICAgICAgICAgIHZhciBvcHRpb25LZXkgPSBrZXkudG9Mb3dlckNhc2UoKTtcbiAgICAgICAgICAgICAgICAvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgQHR5cGVzY3JpcHQtZXNsaW50L2Jhbi10cy1jb21tZW50XG4gICAgICAgICAgICAgICAgLy8gQHRzLWlnbm9yZVxuICAgICAgICAgICAgICAgIG9wdGlvbnNbb3B0aW9uS2V5XSA9IG51bTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgJ0JZV0VFS0RBWSc6XG4gICAgICAgICAgICBjYXNlICdCWURBWSc6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieXdlZWtkYXkgPSBwYXJzZVdlZWtkYXkodmFsdWUpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnRFRTVEFSVCc6XG4gICAgICAgICAgICBjYXNlICdUWklEJzpcbiAgICAgICAgICAgICAgICAvLyBmb3IgYmFja3dhcmRzIGNvbXBhdGliaWxpdHlcbiAgICAgICAgICAgICAgICB2YXIgZHRzdGFydCA9IHBhcnNlRHRzdGFydChsaW5lKTtcbiAgICAgICAgICAgICAgICBvcHRpb25zLnR6aWQgPSBkdHN0YXJ0LnR6aWQ7XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5kdHN0YXJ0ID0gZHRzdGFydC5kdHN0YXJ0O1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnVU5USUwnOlxuICAgICAgICAgICAgICAgIG9wdGlvbnMudW50aWwgPSBkYXRldXRpbC51bnRpbFN0cmluZ1RvRGF0ZSh2YWx1ZSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdCWUVBU1RFUic6XG4gICAgICAgICAgICAgICAgb3B0aW9ucy5ieWVhc3RlciA9IE51bWJlcih2YWx1ZSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihcIlVua25vd24gUlJVTEUgcHJvcGVydHkgJ1wiICsga2V5ICsgXCInXCIpO1xuICAgICAgICB9XG4gICAgfSk7XG4gICAgcmV0dXJuIG9wdGlvbnM7XG59XG5mdW5jdGlvbiBwYXJzZU51bWJlcih2YWx1ZSkge1xuICAgIGlmICh2YWx1ZS5pbmRleE9mKCcsJykgIT09IC0xKSB7XG4gICAgICAgIHZhciB2YWx1ZXMgPSB2YWx1ZS5zcGxpdCgnLCcpO1xuICAgICAgICByZXR1cm4gdmFsdWVzLm1hcChwYXJzZUluZGl2aWR1YWxOdW1iZXIpO1xuICAgIH1cbiAgICByZXR1cm4gcGFyc2VJbmRpdmlkdWFsTnVtYmVyKHZhbHVlKTtcbn1cbmZ1bmN0aW9uIHBhcnNlSW5kaXZpZHVhbE51bWJlcih2YWx1ZSkge1xuICAgIGlmICgvXlsrLV0/XFxkKyQvLnRlc3QodmFsdWUpKSB7XG4gICAgICAgIHJldHVybiBOdW1iZXIodmFsdWUpO1xuICAgIH1cbiAgICByZXR1cm4gdmFsdWU7XG59XG5mdW5jdGlvbiBwYXJzZVdlZWtkYXkodmFsdWUpIHtcbiAgICB2YXIgZGF5cyA9IHZhbHVlLnNwbGl0KCcsJyk7XG4gICAgcmV0dXJuIGRheXMubWFwKGZ1bmN0aW9uIChkYXkpIHtcbiAgICAgICAgaWYgKGRheS5sZW5ndGggPT09IDIpIHtcbiAgICAgICAgICAgIC8vIE1PLCBUVSwgLi4uXG4gICAgICAgICAgICByZXR1cm4gRGF5c1tkYXldOyAvLyB3ZGF5IGluc3RhbmNlb2YgV2Vla2RheVxuICAgICAgICB9XG4gICAgICAgIC8vIC0xTU8sICszRlIsIDFTTywgMTNUVSAuLi5cbiAgICAgICAgdmFyIHBhcnRzID0gZGF5Lm1hdGNoKC9eKFsrLV0/XFxkezEsMn0pKFtBLVpdezJ9KSQvKTtcbiAgICAgICAgaWYgKCFwYXJ0cyB8fCBwYXJ0cy5sZW5ndGggPCAzKSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgU3ludGF4RXJyb3IoXCJJbnZhbGlkIHdlZWtkYXkgc3RyaW5nOiBcIi5jb25jYXQoZGF5KSk7XG4gICAgICAgIH1cbiAgICAgICAgdmFyIG4gPSBOdW1iZXIocGFydHNbMV0pO1xuICAgICAgICB2YXIgd2RheXBhcnQgPSBwYXJ0c1syXTtcbiAgICAgICAgdmFyIHdkYXkgPSBEYXlzW3dkYXlwYXJ0XS53ZWVrZGF5O1xuICAgICAgICByZXR1cm4gbmV3IFdlZWtkYXkod2RheSwgbik7XG4gICAgfSk7XG59XG4vLyMgc291cmNlTWFwcGluZ1VSTD1wYXJzZXN0cmluZy5qcy5tYXAiLCJpbXBvcnQgZGF0ZXV0aWwgZnJvbSAnLi9kYXRldXRpbCc7XG5pbXBvcnQgSXRlclJlc3VsdCBmcm9tICcuL2l0ZXJyZXN1bHQnO1xuaW1wb3J0IENhbGxiYWNrSXRlclJlc3VsdCBmcm9tICcuL2NhbGxiYWNraXRlcnJlc3VsdCc7XG5pbXBvcnQgeyBmcm9tVGV4dCwgcGFyc2VUZXh0LCB0b1RleHQsIGlzRnVsbHlDb252ZXJ0aWJsZSB9IGZyb20gJy4vbmxwL2luZGV4JztcbmltcG9ydCB7IEZyZXF1ZW5jeSwgfSBmcm9tICcuL3R5cGVzJztcbmltcG9ydCB7IHBhcnNlT3B0aW9ucywgaW5pdGlhbGl6ZU9wdGlvbnMgfSBmcm9tICcuL3BhcnNlb3B0aW9ucyc7XG5pbXBvcnQgeyBwYXJzZVN0cmluZyB9IGZyb20gJy4vcGFyc2VzdHJpbmcnO1xuaW1wb3J0IHsgb3B0aW9uc1RvU3RyaW5nIH0gZnJvbSAnLi9vcHRpb25zdG9zdHJpbmcnO1xuaW1wb3J0IHsgQ2FjaGUgfSBmcm9tICcuL2NhY2hlJztcbmltcG9ydCB7IFdlZWtkYXkgfSBmcm9tICcuL3dlZWtkYXknO1xuaW1wb3J0IHsgaXRlciB9IGZyb20gJy4vaXRlci9pbmRleCc7XG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuLy8gUlJ1bGVcbi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG5leHBvcnQgdmFyIERheXMgPSB7XG4gICAgTU86IG5ldyBXZWVrZGF5KDApLFxuICAgIFRVOiBuZXcgV2Vla2RheSgxKSxcbiAgICBXRTogbmV3IFdlZWtkYXkoMiksXG4gICAgVEg6IG5ldyBXZWVrZGF5KDMpLFxuICAgIEZSOiBuZXcgV2Vla2RheSg0KSxcbiAgICBTQTogbmV3IFdlZWtkYXkoNSksXG4gICAgU1U6IG5ldyBXZWVrZGF5KDYpLFxufTtcbmV4cG9ydCB2YXIgREVGQVVMVF9PUFRJT05TID0ge1xuICAgIGZyZXE6IEZyZXF1ZW5jeS5ZRUFSTFksXG4gICAgZHRzdGFydDogbnVsbCxcbiAgICBpbnRlcnZhbDogMSxcbiAgICB3a3N0OiBEYXlzLk1PLFxuICAgIGNvdW50OiBudWxsLFxuICAgIHVudGlsOiBudWxsLFxuICAgIHR6aWQ6IG51bGwsXG4gICAgYnlzZXRwb3M6IG51bGwsXG4gICAgYnltb250aDogbnVsbCxcbiAgICBieW1vbnRoZGF5OiBudWxsLFxuICAgIGJ5bm1vbnRoZGF5OiBudWxsLFxuICAgIGJ5eWVhcmRheTogbnVsbCxcbiAgICBieXdlZWtubzogbnVsbCxcbiAgICBieXdlZWtkYXk6IG51bGwsXG4gICAgYnlud2Vla2RheTogbnVsbCxcbiAgICBieWhvdXI6IG51bGwsXG4gICAgYnltaW51dGU6IG51bGwsXG4gICAgYnlzZWNvbmQ6IG51bGwsXG4gICAgYnllYXN0ZXI6IG51bGwsXG59O1xuZXhwb3J0IHZhciBkZWZhdWx0S2V5cyA9IE9iamVjdC5rZXlzKERFRkFVTFRfT1BUSU9OUyk7XG4vKipcbiAqXG4gKiBAcGFyYW0ge09wdGlvbnM/fSBvcHRpb25zIC0gc2VlIDxodHRwOi8vbGFiaXgub3JnL3B5dGhvbi1kYXRldXRpbC8jaGVhZC1jZjAwNGVlOWE3NTU5Mjc5N2UwNzY3NTJiMmE4ODljMTBmNDQ1NDE4PlxuICogLSBUaGUgb25seSByZXF1aXJlZCBvcHRpb24gaXMgYGZyZXFgLCBvbmUgb2YgUlJ1bGUuWUVBUkxZLCBSUnVsZS5NT05USExZLCAuLi5cbiAqIEBjb25zdHJ1Y3RvclxuICovXG52YXIgUlJ1bGUgPSAvKiogQGNsYXNzICovIChmdW5jdGlvbiAoKSB7XG4gICAgZnVuY3Rpb24gUlJ1bGUob3B0aW9ucywgbm9DYWNoZSkge1xuICAgICAgICBpZiAob3B0aW9ucyA9PT0gdm9pZCAwKSB7IG9wdGlvbnMgPSB7fTsgfVxuICAgICAgICBpZiAobm9DYWNoZSA9PT0gdm9pZCAwKSB7IG5vQ2FjaGUgPSBmYWxzZTsgfVxuICAgICAgICAvLyBSRkMgc3RyaW5nXG4gICAgICAgIHRoaXMuX2NhY2hlID0gbm9DYWNoZSA/IG51bGwgOiBuZXcgQ2FjaGUoKTtcbiAgICAgICAgLy8gdXNlZCBieSB0b1N0cmluZygpXG4gICAgICAgIHRoaXMub3JpZ09wdGlvbnMgPSBpbml0aWFsaXplT3B0aW9ucyhvcHRpb25zKTtcbiAgICAgICAgdmFyIHBhcnNlZE9wdGlvbnMgPSBwYXJzZU9wdGlvbnMob3B0aW9ucykucGFyc2VkT3B0aW9ucztcbiAgICAgICAgdGhpcy5vcHRpb25zID0gcGFyc2VkT3B0aW9ucztcbiAgICB9XG4gICAgUlJ1bGUucGFyc2VUZXh0ID0gZnVuY3Rpb24gKHRleHQsIGxhbmd1YWdlKSB7XG4gICAgICAgIHJldHVybiBwYXJzZVRleHQodGV4dCwgbGFuZ3VhZ2UpO1xuICAgIH07XG4gICAgUlJ1bGUuZnJvbVRleHQgPSBmdW5jdGlvbiAodGV4dCwgbGFuZ3VhZ2UpIHtcbiAgICAgICAgcmV0dXJuIGZyb21UZXh0KHRleHQsIGxhbmd1YWdlKTtcbiAgICB9O1xuICAgIFJSdWxlLmZyb21TdHJpbmcgPSBmdW5jdGlvbiAoc3RyKSB7XG4gICAgICAgIHJldHVybiBuZXcgUlJ1bGUoUlJ1bGUucGFyc2VTdHJpbmcoc3RyKSB8fCB1bmRlZmluZWQpO1xuICAgIH07XG4gICAgUlJ1bGUucHJvdG90eXBlLl9pdGVyID0gZnVuY3Rpb24gKGl0ZXJSZXN1bHQpIHtcbiAgICAgICAgcmV0dXJuIGl0ZXIoaXRlclJlc3VsdCwgdGhpcy5vcHRpb25zKTtcbiAgICB9O1xuICAgIFJSdWxlLnByb3RvdHlwZS5fY2FjaGVHZXQgPSBmdW5jdGlvbiAod2hhdCwgYXJncykge1xuICAgICAgICBpZiAoIXRoaXMuX2NhY2hlKVxuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICByZXR1cm4gdGhpcy5fY2FjaGUuX2NhY2hlR2V0KHdoYXQsIGFyZ3MpO1xuICAgIH07XG4gICAgUlJ1bGUucHJvdG90eXBlLl9jYWNoZUFkZCA9IGZ1bmN0aW9uICh3aGF0LCB2YWx1ZSwgYXJncykge1xuICAgICAgICBpZiAoIXRoaXMuX2NhY2hlKVxuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICByZXR1cm4gdGhpcy5fY2FjaGUuX2NhY2hlQWRkKHdoYXQsIHZhbHVlLCBhcmdzKTtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEBwYXJhbSB7RnVuY3Rpb259IGl0ZXJhdG9yIC0gb3B0aW9uYWwgZnVuY3Rpb24gdGhhdCB3aWxsIGJlIGNhbGxlZFxuICAgICAqIG9uIGVhY2ggZGF0ZSB0aGF0IGlzIGFkZGVkLiBJdCBjYW4gcmV0dXJuIGZhbHNlXG4gICAgICogdG8gc3RvcCB0aGUgaXRlcmF0aW9uLlxuICAgICAqIEByZXR1cm4gQXJyYXkgY29udGFpbmluZyBhbGwgcmVjdXJyZW5jZXMuXG4gICAgICovXG4gICAgUlJ1bGUucHJvdG90eXBlLmFsbCA9IGZ1bmN0aW9uIChpdGVyYXRvcikge1xuICAgICAgICBpZiAoaXRlcmF0b3IpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLl9pdGVyKG5ldyBDYWxsYmFja0l0ZXJSZXN1bHQoJ2FsbCcsIHt9LCBpdGVyYXRvcikpO1xuICAgICAgICB9XG4gICAgICAgIHZhciByZXN1bHQgPSB0aGlzLl9jYWNoZUdldCgnYWxsJyk7XG4gICAgICAgIGlmIChyZXN1bHQgPT09IGZhbHNlKSB7XG4gICAgICAgICAgICByZXN1bHQgPSB0aGlzLl9pdGVyKG5ldyBJdGVyUmVzdWx0KCdhbGwnLCB7fSkpO1xuICAgICAgICAgICAgdGhpcy5fY2FjaGVBZGQoJ2FsbCcsIHJlc3VsdCk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIFJldHVybnMgYWxsIHRoZSBvY2N1cnJlbmNlcyBvZiB0aGUgcnJ1bGUgYmV0d2VlbiBhZnRlciBhbmQgYmVmb3JlLlxuICAgICAqIFRoZSBpbmMga2V5d29yZCBkZWZpbmVzIHdoYXQgaGFwcGVucyBpZiBhZnRlciBhbmQvb3IgYmVmb3JlIGFyZVxuICAgICAqIHRoZW1zZWx2ZXMgb2NjdXJyZW5jZXMuIFdpdGggaW5jID09IFRydWUsIHRoZXkgd2lsbCBiZSBpbmNsdWRlZCBpbiB0aGVcbiAgICAgKiBsaXN0LCBpZiB0aGV5IGFyZSBmb3VuZCBpbiB0aGUgcmVjdXJyZW5jZSBzZXQuXG4gICAgICpcbiAgICAgKiBAcmV0dXJuIEFycmF5XG4gICAgICovXG4gICAgUlJ1bGUucHJvdG90eXBlLmJldHdlZW4gPSBmdW5jdGlvbiAoYWZ0ZXIsIGJlZm9yZSwgaW5jLCBpdGVyYXRvcikge1xuICAgICAgICBpZiAoaW5jID09PSB2b2lkIDApIHsgaW5jID0gZmFsc2U7IH1cbiAgICAgICAgaWYgKCFkYXRldXRpbC5pc1ZhbGlkRGF0ZShhZnRlcikgfHwgIWRhdGV1dGlsLmlzVmFsaWREYXRlKGJlZm9yZSkpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBkYXRlIHBhc3NlZCBpbiB0byBSUnVsZS5iZXR3ZWVuJyk7XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGFyZ3MgPSB7XG4gICAgICAgICAgICBiZWZvcmU6IGJlZm9yZSxcbiAgICAgICAgICAgIGFmdGVyOiBhZnRlcixcbiAgICAgICAgICAgIGluYzogaW5jLFxuICAgICAgICB9O1xuICAgICAgICBpZiAoaXRlcmF0b3IpIHtcbiAgICAgICAgICAgIHJldHVybiB0aGlzLl9pdGVyKG5ldyBDYWxsYmFja0l0ZXJSZXN1bHQoJ2JldHdlZW4nLCBhcmdzLCBpdGVyYXRvcikpO1xuICAgICAgICB9XG4gICAgICAgIHZhciByZXN1bHQgPSB0aGlzLl9jYWNoZUdldCgnYmV0d2VlbicsIGFyZ3MpO1xuICAgICAgICBpZiAocmVzdWx0ID09PSBmYWxzZSkge1xuICAgICAgICAgICAgcmVzdWx0ID0gdGhpcy5faXRlcihuZXcgSXRlclJlc3VsdCgnYmV0d2VlbicsIGFyZ3MpKTtcbiAgICAgICAgICAgIHRoaXMuX2NhY2hlQWRkKCdiZXR3ZWVuJywgcmVzdWx0LCBhcmdzKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH07XG4gICAgLyoqXG4gICAgICogUmV0dXJucyB0aGUgbGFzdCByZWN1cnJlbmNlIGJlZm9yZSB0aGUgZ2l2ZW4gZGF0ZXRpbWUgaW5zdGFuY2UuXG4gICAgICogVGhlIGluYyBrZXl3b3JkIGRlZmluZXMgd2hhdCBoYXBwZW5zIGlmIGR0IGlzIGFuIG9jY3VycmVuY2UuXG4gICAgICogV2l0aCBpbmMgPT0gVHJ1ZSwgaWYgZHQgaXRzZWxmIGlzIGFuIG9jY3VycmVuY2UsIGl0IHdpbGwgYmUgcmV0dXJuZWQuXG4gICAgICpcbiAgICAgKiBAcmV0dXJuIERhdGUgb3IgbnVsbFxuICAgICAqL1xuICAgIFJSdWxlLnByb3RvdHlwZS5iZWZvcmUgPSBmdW5jdGlvbiAoZHQsIGluYykge1xuICAgICAgICBpZiAoaW5jID09PSB2b2lkIDApIHsgaW5jID0gZmFsc2U7IH1cbiAgICAgICAgaWYgKCFkYXRldXRpbC5pc1ZhbGlkRGF0ZShkdCkpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBkYXRlIHBhc3NlZCBpbiB0byBSUnVsZS5iZWZvcmUnKTtcbiAgICAgICAgfVxuICAgICAgICB2YXIgYXJncyA9IHsgZHQ6IGR0LCBpbmM6IGluYyB9O1xuICAgICAgICB2YXIgcmVzdWx0ID0gdGhpcy5fY2FjaGVHZXQoJ2JlZm9yZScsIGFyZ3MpO1xuICAgICAgICBpZiAocmVzdWx0ID09PSBmYWxzZSkge1xuICAgICAgICAgICAgcmVzdWx0ID0gdGhpcy5faXRlcihuZXcgSXRlclJlc3VsdCgnYmVmb3JlJywgYXJncykpO1xuICAgICAgICAgICAgdGhpcy5fY2FjaGVBZGQoJ2JlZm9yZScsIHJlc3VsdCwgYXJncyk7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHJlc3VsdDtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIFJldHVybnMgdGhlIGZpcnN0IHJlY3VycmVuY2UgYWZ0ZXIgdGhlIGdpdmVuIGRhdGV0aW1lIGluc3RhbmNlLlxuICAgICAqIFRoZSBpbmMga2V5d29yZCBkZWZpbmVzIHdoYXQgaGFwcGVucyBpZiBkdCBpcyBhbiBvY2N1cnJlbmNlLlxuICAgICAqIFdpdGggaW5jID09IFRydWUsIGlmIGR0IGl0c2VsZiBpcyBhbiBvY2N1cnJlbmNlLCBpdCB3aWxsIGJlIHJldHVybmVkLlxuICAgICAqXG4gICAgICogQHJldHVybiBEYXRlIG9yIG51bGxcbiAgICAgKi9cbiAgICBSUnVsZS5wcm90b3R5cGUuYWZ0ZXIgPSBmdW5jdGlvbiAoZHQsIGluYykge1xuICAgICAgICBpZiAoaW5jID09PSB2b2lkIDApIHsgaW5jID0gZmFsc2U7IH1cbiAgICAgICAgaWYgKCFkYXRldXRpbC5pc1ZhbGlkRGF0ZShkdCkpIHtcbiAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcignSW52YWxpZCBkYXRlIHBhc3NlZCBpbiB0byBSUnVsZS5hZnRlcicpO1xuICAgICAgICB9XG4gICAgICAgIHZhciBhcmdzID0geyBkdDogZHQsIGluYzogaW5jIH07XG4gICAgICAgIHZhciByZXN1bHQgPSB0aGlzLl9jYWNoZUdldCgnYWZ0ZXInLCBhcmdzKTtcbiAgICAgICAgaWYgKHJlc3VsdCA9PT0gZmFsc2UpIHtcbiAgICAgICAgICAgIHJlc3VsdCA9IHRoaXMuX2l0ZXIobmV3IEl0ZXJSZXN1bHQoJ2FmdGVyJywgYXJncykpO1xuICAgICAgICAgICAgdGhpcy5fY2FjaGVBZGQoJ2FmdGVyJywgcmVzdWx0LCBhcmdzKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH07XG4gICAgLyoqXG4gICAgICogUmV0dXJucyB0aGUgbnVtYmVyIG9mIHJlY3VycmVuY2VzIGluIHRoaXMgc2V0LiBJdCB3aWxsIGhhdmUgZ28gdHJvdWdoXG4gICAgICogdGhlIHdob2xlIHJlY3VycmVuY2UsIGlmIHRoaXMgaGFzbid0IGJlZW4gZG9uZSBiZWZvcmUuXG4gICAgICovXG4gICAgUlJ1bGUucHJvdG90eXBlLmNvdW50ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5hbGwoKS5sZW5ndGg7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBDb252ZXJ0cyB0aGUgcnJ1bGUgaW50byBpdHMgc3RyaW5nIHJlcHJlc2VudGF0aW9uXG4gICAgICpcbiAgICAgKiBAc2VlIDxodHRwOi8vd3d3LmlldGYub3JnL3JmYy9yZmMyNDQ1LnR4dD5cbiAgICAgKiBAcmV0dXJuIFN0cmluZ1xuICAgICAqL1xuICAgIFJSdWxlLnByb3RvdHlwZS50b1N0cmluZyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIG9wdGlvbnNUb1N0cmluZyh0aGlzLm9yaWdPcHRpb25zKTtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIFdpbGwgY29udmVydCBhbGwgcnVsZXMgZGVzY3JpYmVkIGluIG5scDpUb1RleHRcbiAgICAgKiB0byB0ZXh0LlxuICAgICAqL1xuICAgIFJSdWxlLnByb3RvdHlwZS50b1RleHQgPSBmdW5jdGlvbiAoZ2V0dGV4dCwgbGFuZ3VhZ2UsIGRhdGVGb3JtYXR0ZXIpIHtcbiAgICAgICAgcmV0dXJuIHRvVGV4dCh0aGlzLCBnZXR0ZXh0LCBsYW5ndWFnZSwgZGF0ZUZvcm1hdHRlcik7XG4gICAgfTtcbiAgICBSUnVsZS5wcm90b3R5cGUuaXNGdWxseUNvbnZlcnRpYmxlVG9UZXh0ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gaXNGdWxseUNvbnZlcnRpYmxlKHRoaXMpO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogQHJldHVybiBhIFJSdWxlIGluc3RhbmNlIHdpdGggdGhlIHNhbWUgZnJlcSBhbmQgb3B0aW9uc1xuICAgICAqIGFzIHRoaXMgb25lIChjYWNoZSBpcyBub3QgY2xvbmVkKVxuICAgICAqL1xuICAgIFJSdWxlLnByb3RvdHlwZS5jbG9uZSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIG5ldyBSUnVsZSh0aGlzLm9yaWdPcHRpb25zKTtcbiAgICB9O1xuICAgIC8vIFJSdWxlIGNsYXNzICdjb25zdGFudHMnXG4gICAgUlJ1bGUuRlJFUVVFTkNJRVMgPSBbXG4gICAgICAgICdZRUFSTFknLFxuICAgICAgICAnTU9OVEhMWScsXG4gICAgICAgICdXRUVLTFknLFxuICAgICAgICAnREFJTFknLFxuICAgICAgICAnSE9VUkxZJyxcbiAgICAgICAgJ01JTlVURUxZJyxcbiAgICAgICAgJ1NFQ09ORExZJyxcbiAgICBdO1xuICAgIFJSdWxlLllFQVJMWSA9IEZyZXF1ZW5jeS5ZRUFSTFk7XG4gICAgUlJ1bGUuTU9OVEhMWSA9IEZyZXF1ZW5jeS5NT05USExZO1xuICAgIFJSdWxlLldFRUtMWSA9IEZyZXF1ZW5jeS5XRUVLTFk7XG4gICAgUlJ1bGUuREFJTFkgPSBGcmVxdWVuY3kuREFJTFk7XG4gICAgUlJ1bGUuSE9VUkxZID0gRnJlcXVlbmN5LkhPVVJMWTtcbiAgICBSUnVsZS5NSU5VVEVMWSA9IEZyZXF1ZW5jeS5NSU5VVEVMWTtcbiAgICBSUnVsZS5TRUNPTkRMWSA9IEZyZXF1ZW5jeS5TRUNPTkRMWTtcbiAgICBSUnVsZS5NTyA9IERheXMuTU87XG4gICAgUlJ1bGUuVFUgPSBEYXlzLlRVO1xuICAgIFJSdWxlLldFID0gRGF5cy5XRTtcbiAgICBSUnVsZS5USCA9IERheXMuVEg7XG4gICAgUlJ1bGUuRlIgPSBEYXlzLkZSO1xuICAgIFJSdWxlLlNBID0gRGF5cy5TQTtcbiAgICBSUnVsZS5TVSA9IERheXMuU1U7XG4gICAgUlJ1bGUucGFyc2VTdHJpbmcgPSBwYXJzZVN0cmluZztcbiAgICBSUnVsZS5vcHRpb25zVG9TdHJpbmcgPSBvcHRpb25zVG9TdHJpbmc7XG4gICAgcmV0dXJuIFJSdWxlO1xufSgpKTtcbmV4cG9ydCB7IFJSdWxlIH07XG4vLyMgc291cmNlTWFwcGluZ1VSTD1ycnVsZS5qcy5tYXAiLCJpbXBvcnQgeyBfX2V4dGVuZHMgfSBmcm9tIFwidHNsaWJcIjtcbmltcG9ydCB7IFJSdWxlIH0gZnJvbSAnLi9ycnVsZSc7XG5pbXBvcnQgZGF0ZXV0aWwgZnJvbSAnLi9kYXRldXRpbCc7XG5pbXBvcnQgeyBpbmNsdWRlcyB9IGZyb20gJy4vaGVscGVycyc7XG5pbXBvcnQgeyBpdGVyU2V0IH0gZnJvbSAnLi9pdGVyc2V0JztcbmltcG9ydCB7IHJydWxlc3RyIH0gZnJvbSAnLi9ycnVsZXN0cic7XG5pbXBvcnQgeyBvcHRpb25zVG9TdHJpbmcgfSBmcm9tICcuL29wdGlvbnN0b3N0cmluZyc7XG5mdW5jdGlvbiBjcmVhdGVHZXR0ZXJTZXR0ZXIoZmllbGROYW1lKSB7XG4gICAgdmFyIF90aGlzID0gdGhpcztcbiAgICByZXR1cm4gZnVuY3Rpb24gKGZpZWxkKSB7XG4gICAgICAgIGlmIChmaWVsZCAhPT0gdW5kZWZpbmVkKSB7XG4gICAgICAgICAgICBfdGhpc1tcIl9cIi5jb25jYXQoZmllbGROYW1lKV0gPSBmaWVsZDtcbiAgICAgICAgfVxuICAgICAgICBpZiAoX3RoaXNbXCJfXCIuY29uY2F0KGZpZWxkTmFtZSldICE9PSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHJldHVybiBfdGhpc1tcIl9cIi5jb25jYXQoZmllbGROYW1lKV07XG4gICAgICAgIH1cbiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCBfdGhpcy5fcnJ1bGUubGVuZ3RoOyBpKyspIHtcbiAgICAgICAgICAgIHZhciBmaWVsZF8xID0gX3RoaXMuX3JydWxlW2ldLm9yaWdPcHRpb25zW2ZpZWxkTmFtZV07XG4gICAgICAgICAgICBpZiAoZmllbGRfMSkge1xuICAgICAgICAgICAgICAgIHJldHVybiBmaWVsZF8xO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfTtcbn1cbnZhciBSUnVsZVNldCA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uIChfc3VwZXIpIHtcbiAgICBfX2V4dGVuZHMoUlJ1bGVTZXQsIF9zdXBlcik7XG4gICAgLyoqXG4gICAgICpcbiAgICAgKiBAcGFyYW0ge0Jvb2xlYW4/fSBub0NhY2hlXG4gICAgICogVGhlIHNhbWUgc3RyYXRhZ3kgYXMgUlJ1bGUgb24gY2FjaGUsIGRlZmF1bHQgdG8gZmFsc2VcbiAgICAgKiBAY29uc3RydWN0b3JcbiAgICAgKi9cbiAgICBmdW5jdGlvbiBSUnVsZVNldChub0NhY2hlKSB7XG4gICAgICAgIGlmIChub0NhY2hlID09PSB2b2lkIDApIHsgbm9DYWNoZSA9IGZhbHNlOyB9XG4gICAgICAgIHZhciBfdGhpcyA9IF9zdXBlci5jYWxsKHRoaXMsIHt9LCBub0NhY2hlKSB8fCB0aGlzO1xuICAgICAgICBfdGhpcy5kdHN0YXJ0ID0gY3JlYXRlR2V0dGVyU2V0dGVyLmFwcGx5KF90aGlzLCBbJ2R0c3RhcnQnXSk7XG4gICAgICAgIF90aGlzLnR6aWQgPSBjcmVhdGVHZXR0ZXJTZXR0ZXIuYXBwbHkoX3RoaXMsIFsndHppZCddKTtcbiAgICAgICAgX3RoaXMuX3JydWxlID0gW107XG4gICAgICAgIF90aGlzLl9yZGF0ZSA9IFtdO1xuICAgICAgICBfdGhpcy5fZXhydWxlID0gW107XG4gICAgICAgIF90aGlzLl9leGRhdGUgPSBbXTtcbiAgICAgICAgcmV0dXJuIF90aGlzO1xuICAgIH1cbiAgICBSUnVsZVNldC5wcm90b3R5cGUuX2l0ZXIgPSBmdW5jdGlvbiAoaXRlclJlc3VsdCkge1xuICAgICAgICByZXR1cm4gaXRlclNldChpdGVyUmVzdWx0LCB0aGlzLl9ycnVsZSwgdGhpcy5fZXhydWxlLCB0aGlzLl9yZGF0ZSwgdGhpcy5fZXhkYXRlLCB0aGlzLnR6aWQoKSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBBZGRzIGFuIFJSdWxlIHRvIHRoZSBzZXRcbiAgICAgKlxuICAgICAqIEBwYXJhbSB7UlJ1bGV9XG4gICAgICovXG4gICAgUlJ1bGVTZXQucHJvdG90eXBlLnJydWxlID0gZnVuY3Rpb24gKHJydWxlKSB7XG4gICAgICAgIF9hZGRSdWxlKHJydWxlLCB0aGlzLl9ycnVsZSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBBZGRzIGFuIEVYUlVMRSB0byB0aGUgc2V0XG4gICAgICpcbiAgICAgKiBAcGFyYW0ge1JSdWxlfVxuICAgICAqL1xuICAgIFJSdWxlU2V0LnByb3RvdHlwZS5leHJ1bGUgPSBmdW5jdGlvbiAocnJ1bGUpIHtcbiAgICAgICAgX2FkZFJ1bGUocnJ1bGUsIHRoaXMuX2V4cnVsZSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBBZGRzIGFuIFJEYXRlIHRvIHRoZSBzZXRcbiAgICAgKlxuICAgICAqIEBwYXJhbSB7RGF0ZX1cbiAgICAgKi9cbiAgICBSUnVsZVNldC5wcm90b3R5cGUucmRhdGUgPSBmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICBfYWRkRGF0ZShkYXRlLCB0aGlzLl9yZGF0ZSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBBZGRzIGFuIEVYREFURSB0byB0aGUgc2V0XG4gICAgICpcbiAgICAgKiBAcGFyYW0ge0RhdGV9XG4gICAgICovXG4gICAgUlJ1bGVTZXQucHJvdG90eXBlLmV4ZGF0ZSA9IGZ1bmN0aW9uIChkYXRlKSB7XG4gICAgICAgIF9hZGREYXRlKGRhdGUsIHRoaXMuX2V4ZGF0ZSk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBHZXQgbGlzdCBvZiBpbmNsdWRlZCBycnVsZXMgaW4gdGhpcyByZWN1cnJlbmNlIHNldC5cbiAgICAgKlxuICAgICAqIEByZXR1cm4gTGlzdCBvZiBycnVsZXNcbiAgICAgKi9cbiAgICBSUnVsZVNldC5wcm90b3R5cGUucnJ1bGVzID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5fcnJ1bGUubWFwKGZ1bmN0aW9uIChlKSB7IHJldHVybiBycnVsZXN0cihlLnRvU3RyaW5nKCkpOyB9KTtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGV4Y2x1ZGVkIHJydWxlcyBpbiB0aGlzIHJlY3VycmVuY2Ugc2V0LlxuICAgICAqXG4gICAgICogQHJldHVybiBMaXN0IG9mIGV4cnVsZXNcbiAgICAgKi9cbiAgICBSUnVsZVNldC5wcm90b3R5cGUuZXhydWxlcyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX2V4cnVsZS5tYXAoZnVuY3Rpb24gKGUpIHsgcmV0dXJuIHJydWxlc3RyKGUudG9TdHJpbmcoKSk7IH0pO1xuICAgIH07XG4gICAgLyoqXG4gICAgICogR2V0IGxpc3Qgb2YgaW5jbHVkZWQgZGF0ZXRpbWVzIGluIHRoaXMgcmVjdXJyZW5jZSBzZXQuXG4gICAgICpcbiAgICAgKiBAcmV0dXJuIExpc3Qgb2YgcmRhdGVzXG4gICAgICovXG4gICAgUlJ1bGVTZXQucHJvdG90eXBlLnJkYXRlcyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX3JkYXRlLm1hcChmdW5jdGlvbiAoZSkgeyByZXR1cm4gbmV3IERhdGUoZS5nZXRUaW1lKCkpOyB9KTtcbiAgICB9O1xuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGluY2x1ZGVkIGRhdGV0aW1lcyBpbiB0aGlzIHJlY3VycmVuY2Ugc2V0LlxuICAgICAqXG4gICAgICogQHJldHVybiBMaXN0IG9mIGV4ZGF0ZXNcbiAgICAgKi9cbiAgICBSUnVsZVNldC5wcm90b3R5cGUuZXhkYXRlcyA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMuX2V4ZGF0ZS5tYXAoZnVuY3Rpb24gKGUpIHsgcmV0dXJuIG5ldyBEYXRlKGUuZ2V0VGltZSgpKTsgfSk7XG4gICAgfTtcbiAgICBSUnVsZVNldC5wcm90b3R5cGUudmFsdWVPZiA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdmFyIHJlc3VsdCA9IFtdO1xuICAgICAgICBpZiAoIXRoaXMuX3JydWxlLmxlbmd0aCAmJiB0aGlzLl9kdHN0YXJ0KSB7XG4gICAgICAgICAgICByZXN1bHQgPSByZXN1bHQuY29uY2F0KG9wdGlvbnNUb1N0cmluZyh7IGR0c3RhcnQ6IHRoaXMuX2R0c3RhcnQgfSkpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX3JydWxlLmZvckVhY2goZnVuY3Rpb24gKHJydWxlKSB7XG4gICAgICAgICAgICByZXN1bHQgPSByZXN1bHQuY29uY2F0KHJydWxlLnRvU3RyaW5nKCkuc3BsaXQoJ1xcbicpKTtcbiAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuX2V4cnVsZS5mb3JFYWNoKGZ1bmN0aW9uIChleHJ1bGUpIHtcbiAgICAgICAgICAgIHJlc3VsdCA9IHJlc3VsdC5jb25jYXQoZXhydWxlXG4gICAgICAgICAgICAgICAgLnRvU3RyaW5nKClcbiAgICAgICAgICAgICAgICAuc3BsaXQoJ1xcbicpXG4gICAgICAgICAgICAgICAgLm1hcChmdW5jdGlvbiAobGluZSkgeyByZXR1cm4gbGluZS5yZXBsYWNlKC9eUlJVTEU6LywgJ0VYUlVMRTonKTsgfSlcbiAgICAgICAgICAgICAgICAuZmlsdGVyKGZ1bmN0aW9uIChsaW5lKSB7IHJldHVybiAhL15EVFNUQVJULy50ZXN0KGxpbmUpOyB9KSk7XG4gICAgICAgIH0pO1xuICAgICAgICBpZiAodGhpcy5fcmRhdGUubGVuZ3RoKSB7XG4gICAgICAgICAgICByZXN1bHQucHVzaChyZGF0ZXNUb1N0cmluZygnUkRBVEUnLCB0aGlzLl9yZGF0ZSwgdGhpcy50emlkKCkpKTtcbiAgICAgICAgfVxuICAgICAgICBpZiAodGhpcy5fZXhkYXRlLmxlbmd0aCkge1xuICAgICAgICAgICAgcmVzdWx0LnB1c2gocmRhdGVzVG9TdHJpbmcoJ0VYREFURScsIHRoaXMuX2V4ZGF0ZSwgdGhpcy50emlkKCkpKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gcmVzdWx0O1xuICAgIH07XG4gICAgLyoqXG4gICAgICogdG8gZ2VuZXJhdGUgcmVjdXJyZW5jZSBmaWVsZCBzdWNoIGFzOlxuICAgICAqIERUU1RBUlQ6MTk5NzA5MDJUMDEwMDAwWlxuICAgICAqIFJSVUxFOkZSRVE9WUVBUkxZO0NPVU5UPTI7QllEQVk9VFVcbiAgICAgKiBSUlVMRTpGUkVRPVlFQVJMWTtDT1VOVD0xO0JZREFZPVRIXG4gICAgICovXG4gICAgUlJ1bGVTZXQucHJvdG90eXBlLnRvU3RyaW5nID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy52YWx1ZU9mKCkuam9pbignXFxuJyk7XG4gICAgfTtcbiAgICAvKipcbiAgICAgKiBDcmVhdGUgYSBuZXcgUlJ1bGVTZXQgT2JqZWN0IGNvbXBsZXRlbHkgYmFzZSBvbiBjdXJyZW50IGluc3RhbmNlXG4gICAgICovXG4gICAgUlJ1bGVTZXQucHJvdG90eXBlLmNsb25lID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgcnJzID0gbmV3IFJSdWxlU2V0KCEhdGhpcy5fY2FjaGUpO1xuICAgICAgICB0aGlzLl9ycnVsZS5mb3JFYWNoKGZ1bmN0aW9uIChydWxlKSB7IHJldHVybiBycnMucnJ1bGUocnVsZS5jbG9uZSgpKTsgfSk7XG4gICAgICAgIHRoaXMuX2V4cnVsZS5mb3JFYWNoKGZ1bmN0aW9uIChydWxlKSB7IHJldHVybiBycnMuZXhydWxlKHJ1bGUuY2xvbmUoKSk7IH0pO1xuICAgICAgICB0aGlzLl9yZGF0ZS5mb3JFYWNoKGZ1bmN0aW9uIChkYXRlKSB7IHJldHVybiBycnMucmRhdGUobmV3IERhdGUoZGF0ZS5nZXRUaW1lKCkpKTsgfSk7XG4gICAgICAgIHRoaXMuX2V4ZGF0ZS5mb3JFYWNoKGZ1bmN0aW9uIChkYXRlKSB7IHJldHVybiBycnMuZXhkYXRlKG5ldyBEYXRlKGRhdGUuZ2V0VGltZSgpKSk7IH0pO1xuICAgICAgICByZXR1cm4gcnJzO1xuICAgIH07XG4gICAgcmV0dXJuIFJSdWxlU2V0O1xufShSUnVsZSkpO1xuZXhwb3J0IHsgUlJ1bGVTZXQgfTtcbmZ1bmN0aW9uIF9hZGRSdWxlKHJydWxlLCBjb2xsZWN0aW9uKSB7XG4gICAgaWYgKCEocnJ1bGUgaW5zdGFuY2VvZiBSUnVsZSkpIHtcbiAgICAgICAgdGhyb3cgbmV3IFR5cGVFcnJvcihTdHJpbmcocnJ1bGUpICsgJyBpcyBub3QgUlJ1bGUgaW5zdGFuY2UnKTtcbiAgICB9XG4gICAgaWYgKCFpbmNsdWRlcyhjb2xsZWN0aW9uLm1hcChTdHJpbmcpLCBTdHJpbmcocnJ1bGUpKSkge1xuICAgICAgICBjb2xsZWN0aW9uLnB1c2gocnJ1bGUpO1xuICAgIH1cbn1cbmZ1bmN0aW9uIF9hZGREYXRlKGRhdGUsIGNvbGxlY3Rpb24pIHtcbiAgICBpZiAoIShkYXRlIGluc3RhbmNlb2YgRGF0ZSkpIHtcbiAgICAgICAgdGhyb3cgbmV3IFR5cGVFcnJvcihTdHJpbmcoZGF0ZSkgKyAnIGlzIG5vdCBEYXRlIGluc3RhbmNlJyk7XG4gICAgfVxuICAgIGlmICghaW5jbHVkZXMoY29sbGVjdGlvbi5tYXAoTnVtYmVyKSwgTnVtYmVyKGRhdGUpKSkge1xuICAgICAgICBjb2xsZWN0aW9uLnB1c2goZGF0ZSk7XG4gICAgICAgIGRhdGV1dGlsLnNvcnQoY29sbGVjdGlvbik7XG4gICAgfVxufVxuZnVuY3Rpb24gcmRhdGVzVG9TdHJpbmcocGFyYW0sIHJkYXRlcywgdHppZCkge1xuICAgIHZhciBpc1VUQyA9ICF0emlkIHx8IHR6aWQudG9VcHBlckNhc2UoKSA9PT0gJ1VUQyc7XG4gICAgdmFyIGhlYWRlciA9IGlzVVRDID8gXCJcIi5jb25jYXQocGFyYW0sIFwiOlwiKSA6IFwiXCIuY29uY2F0KHBhcmFtLCBcIjtUWklEPVwiKS5jb25jYXQodHppZCwgXCI6XCIpO1xuICAgIHZhciBkYXRlU3RyaW5nID0gcmRhdGVzXG4gICAgICAgIC5tYXAoZnVuY3Rpb24gKHJkYXRlKSB7IHJldHVybiBkYXRldXRpbC50aW1lVG9VbnRpbFN0cmluZyhyZGF0ZS52YWx1ZU9mKCksIGlzVVRDKTsgfSlcbiAgICAgICAgLmpvaW4oJywnKTtcbiAgICByZXR1cm4gXCJcIi5jb25jYXQoaGVhZGVyKS5jb25jYXQoZGF0ZVN0cmluZyk7XG59XG4vLyMgc291cmNlTWFwcGluZ1VSTD1ycnVsZXNldC5qcy5tYXAiLCJpbXBvcnQgeyBfX2Fzc2lnbiB9IGZyb20gXCJ0c2xpYlwiO1xuaW1wb3J0IHsgUlJ1bGUgfSBmcm9tICcuL3JydWxlJztcbmltcG9ydCB7IFJSdWxlU2V0IH0gZnJvbSAnLi9ycnVsZXNldCc7XG5pbXBvcnQgZGF0ZXV0aWwgZnJvbSAnLi9kYXRldXRpbCc7XG5pbXBvcnQgeyBpbmNsdWRlcywgc3BsaXQgfSBmcm9tICcuL2hlbHBlcnMnO1xuaW1wb3J0IHsgcGFyc2VTdHJpbmcsIHBhcnNlRHRzdGFydCB9IGZyb20gJy4vcGFyc2VzdHJpbmcnO1xuLyoqXG4gKiBSUnVsZVN0clxuICogVG8gcGFyc2UgYSBzZXQgb2YgcnJ1bGUgc3RyaW5nc1xuICovXG52YXIgREVGQVVMVF9PUFRJT05TID0ge1xuICAgIGR0c3RhcnQ6IG51bGwsXG4gICAgY2FjaGU6IGZhbHNlLFxuICAgIHVuZm9sZDogZmFsc2UsXG4gICAgZm9yY2VzZXQ6IGZhbHNlLFxuICAgIGNvbXBhdGlibGU6IGZhbHNlLFxuICAgIHR6aWQ6IG51bGwsXG59O1xuZXhwb3J0IGZ1bmN0aW9uIHBhcnNlSW5wdXQocywgb3B0aW9ucykge1xuICAgIHZhciBycnVsZXZhbHMgPSBbXTtcbiAgICB2YXIgcmRhdGV2YWxzID0gW107XG4gICAgdmFyIGV4cnVsZXZhbHMgPSBbXTtcbiAgICB2YXIgZXhkYXRldmFscyA9IFtdO1xuICAgIHZhciBwYXJzZWREdHN0YXJ0ID0gcGFyc2VEdHN0YXJ0KHMpO1xuICAgIHZhciBkdHN0YXJ0ID0gcGFyc2VkRHRzdGFydC5kdHN0YXJ0O1xuICAgIHZhciB0emlkID0gcGFyc2VkRHRzdGFydC50emlkO1xuICAgIHZhciBsaW5lcyA9IHNwbGl0SW50b0xpbmVzKHMsIG9wdGlvbnMudW5mb2xkKTtcbiAgICBsaW5lcy5mb3JFYWNoKGZ1bmN0aW9uIChsaW5lKSB7XG4gICAgICAgIHZhciBfYTtcbiAgICAgICAgaWYgKCFsaW5lKVxuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB2YXIgX2IgPSBicmVha0Rvd25MaW5lKGxpbmUpLCBuYW1lID0gX2IubmFtZSwgcGFybXMgPSBfYi5wYXJtcywgdmFsdWUgPSBfYi52YWx1ZTtcbiAgICAgICAgc3dpdGNoIChuYW1lLnRvVXBwZXJDYXNlKCkpIHtcbiAgICAgICAgICAgIGNhc2UgJ1JSVUxFJzpcbiAgICAgICAgICAgICAgICBpZiAocGFybXMubGVuZ3RoKSB7XG4gICAgICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcihcInVuc3VwcG9ydGVkIFJSVUxFIHBhcm06IFwiLmNvbmNhdChwYXJtcy5qb2luKCcsJykpKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcnJ1bGV2YWxzLnB1c2gocGFyc2VTdHJpbmcobGluZSkpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnUkRBVEUnOlxuICAgICAgICAgICAgICAgIHZhciBfYyA9IChfYSA9IC9SREFURSg/OjtUWklEPShbXjo9XSspKT8vaS5leGVjKGxpbmUpKSAhPT0gbnVsbCAmJiBfYSAhPT0gdm9pZCAwID8gX2EgOiBbXSwgcmRhdGVUemlkID0gX2NbMV07XG4gICAgICAgICAgICAgICAgaWYgKHJkYXRlVHppZCAmJiAhdHppZCkge1xuICAgICAgICAgICAgICAgICAgICB0emlkID0gcmRhdGVUemlkO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICByZGF0ZXZhbHMgPSByZGF0ZXZhbHMuY29uY2F0KHBhcnNlUkRhdGUodmFsdWUsIHBhcm1zKSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdFWFJVTEUnOlxuICAgICAgICAgICAgICAgIGlmIChwYXJtcy5sZW5ndGgpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhyb3cgbmV3IEVycm9yKFwidW5zdXBwb3J0ZWQgRVhSVUxFIHBhcm06IFwiLmNvbmNhdChwYXJtcy5qb2luKCcsJykpKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZXhydWxldmFscy5wdXNoKHBhcnNlU3RyaW5nKHZhbHVlKSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBjYXNlICdFWERBVEUnOlxuICAgICAgICAgICAgICAgIGV4ZGF0ZXZhbHMgPSBleGRhdGV2YWxzLmNvbmNhdChwYXJzZVJEYXRlKHZhbHVlLCBwYXJtcykpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICAgICAgY2FzZSAnRFRTVEFSVCc6XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICBkZWZhdWx0OlxuICAgICAgICAgICAgICAgIHRocm93IG5ldyBFcnJvcigndW5zdXBwb3J0ZWQgcHJvcGVydHk6ICcgKyBuYW1lKTtcbiAgICAgICAgfVxuICAgIH0pO1xuICAgIHJldHVybiB7XG4gICAgICAgIGR0c3RhcnQ6IGR0c3RhcnQsXG4gICAgICAgIHR6aWQ6IHR6aWQsXG4gICAgICAgIHJydWxldmFsczogcnJ1bGV2YWxzLFxuICAgICAgICByZGF0ZXZhbHM6IHJkYXRldmFscyxcbiAgICAgICAgZXhydWxldmFsczogZXhydWxldmFscyxcbiAgICAgICAgZXhkYXRldmFsczogZXhkYXRldmFscyxcbiAgICB9O1xufVxuZnVuY3Rpb24gYnVpbGRSdWxlKHMsIG9wdGlvbnMpIHtcbiAgICB2YXIgX2EgPSBwYXJzZUlucHV0KHMsIG9wdGlvbnMpLCBycnVsZXZhbHMgPSBfYS5ycnVsZXZhbHMsIHJkYXRldmFscyA9IF9hLnJkYXRldmFscywgZXhydWxldmFscyA9IF9hLmV4cnVsZXZhbHMsIGV4ZGF0ZXZhbHMgPSBfYS5leGRhdGV2YWxzLCBkdHN0YXJ0ID0gX2EuZHRzdGFydCwgdHppZCA9IF9hLnR6aWQ7XG4gICAgdmFyIG5vQ2FjaGUgPSBvcHRpb25zLmNhY2hlID09PSBmYWxzZTtcbiAgICBpZiAob3B0aW9ucy5jb21wYXRpYmxlKSB7XG4gICAgICAgIG9wdGlvbnMuZm9yY2VzZXQgPSB0cnVlO1xuICAgICAgICBvcHRpb25zLnVuZm9sZCA9IHRydWU7XG4gICAgfVxuICAgIGlmIChvcHRpb25zLmZvcmNlc2V0IHx8XG4gICAgICAgIHJydWxldmFscy5sZW5ndGggPiAxIHx8XG4gICAgICAgIHJkYXRldmFscy5sZW5ndGggfHxcbiAgICAgICAgZXhydWxldmFscy5sZW5ndGggfHxcbiAgICAgICAgZXhkYXRldmFscy5sZW5ndGgpIHtcbiAgICAgICAgdmFyIHJzZXRfMSA9IG5ldyBSUnVsZVNldChub0NhY2hlKTtcbiAgICAgICAgcnNldF8xLmR0c3RhcnQoZHRzdGFydCk7XG4gICAgICAgIHJzZXRfMS50emlkKHR6aWQgfHwgdW5kZWZpbmVkKTtcbiAgICAgICAgcnJ1bGV2YWxzLmZvckVhY2goZnVuY3Rpb24gKHZhbCkge1xuICAgICAgICAgICAgcnNldF8xLnJydWxlKG5ldyBSUnVsZShncm9vbVJydWxlT3B0aW9ucyh2YWwsIGR0c3RhcnQsIHR6aWQpLCBub0NhY2hlKSk7XG4gICAgICAgIH0pO1xuICAgICAgICByZGF0ZXZhbHMuZm9yRWFjaChmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICAgICAgcnNldF8xLnJkYXRlKGRhdGUpO1xuICAgICAgICB9KTtcbiAgICAgICAgZXhydWxldmFscy5mb3JFYWNoKGZ1bmN0aW9uICh2YWwpIHtcbiAgICAgICAgICAgIHJzZXRfMS5leHJ1bGUobmV3IFJSdWxlKGdyb29tUnJ1bGVPcHRpb25zKHZhbCwgZHRzdGFydCwgdHppZCksIG5vQ2FjaGUpKTtcbiAgICAgICAgfSk7XG4gICAgICAgIGV4ZGF0ZXZhbHMuZm9yRWFjaChmdW5jdGlvbiAoZGF0ZSkge1xuICAgICAgICAgICAgcnNldF8xLmV4ZGF0ZShkYXRlKTtcbiAgICAgICAgfSk7XG4gICAgICAgIGlmIChvcHRpb25zLmNvbXBhdGlibGUgJiYgb3B0aW9ucy5kdHN0YXJ0KVxuICAgICAgICAgICAgcnNldF8xLnJkYXRlKGR0c3RhcnQpO1xuICAgICAgICByZXR1cm4gcnNldF8xO1xuICAgIH1cbiAgICB2YXIgdmFsID0gcnJ1bGV2YWxzWzBdIHx8IHt9O1xuICAgIHJldHVybiBuZXcgUlJ1bGUoZ3Jvb21ScnVsZU9wdGlvbnModmFsLCB2YWwuZHRzdGFydCB8fCBvcHRpb25zLmR0c3RhcnQgfHwgZHRzdGFydCwgdmFsLnR6aWQgfHwgb3B0aW9ucy50emlkIHx8IHR6aWQpLCBub0NhY2hlKTtcbn1cbmV4cG9ydCBmdW5jdGlvbiBycnVsZXN0cihzLCBvcHRpb25zKSB7XG4gICAgaWYgKG9wdGlvbnMgPT09IHZvaWQgMCkgeyBvcHRpb25zID0ge307IH1cbiAgICByZXR1cm4gYnVpbGRSdWxlKHMsIGluaXRpYWxpemVPcHRpb25zKG9wdGlvbnMpKTtcbn1cbmZ1bmN0aW9uIGdyb29tUnJ1bGVPcHRpb25zKHZhbCwgZHRzdGFydCwgdHppZCkge1xuICAgIHJldHVybiBfX2Fzc2lnbihfX2Fzc2lnbih7fSwgdmFsKSwgeyBkdHN0YXJ0OiBkdHN0YXJ0LCB0emlkOiB0emlkIH0pO1xufVxuZnVuY3Rpb24gaW5pdGlhbGl6ZU9wdGlvbnMob3B0aW9ucykge1xuICAgIHZhciBpbnZhbGlkID0gW107XG4gICAgdmFyIGtleXMgPSBPYmplY3Qua2V5cyhvcHRpb25zKTtcbiAgICB2YXIgZGVmYXVsdEtleXMgPSBPYmplY3Qua2V5cyhERUZBVUxUX09QVElPTlMpO1xuICAgIGtleXMuZm9yRWFjaChmdW5jdGlvbiAoa2V5KSB7XG4gICAgICAgIGlmICghaW5jbHVkZXMoZGVmYXVsdEtleXMsIGtleSkpXG4gICAgICAgICAgICBpbnZhbGlkLnB1c2goa2V5KTtcbiAgICB9KTtcbiAgICBpZiAoaW52YWxpZC5sZW5ndGgpIHtcbiAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdJbnZhbGlkIG9wdGlvbnM6ICcgKyBpbnZhbGlkLmpvaW4oJywgJykpO1xuICAgIH1cbiAgICByZXR1cm4gX19hc3NpZ24oX19hc3NpZ24oe30sIERFRkFVTFRfT1BUSU9OUyksIG9wdGlvbnMpO1xufVxuZnVuY3Rpb24gZXh0cmFjdE5hbWUobGluZSkge1xuICAgIGlmIChsaW5lLmluZGV4T2YoJzonKSA9PT0gLTEpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgIG5hbWU6ICdSUlVMRScsXG4gICAgICAgICAgICB2YWx1ZTogbGluZSxcbiAgICAgICAgfTtcbiAgICB9XG4gICAgdmFyIF9hID0gc3BsaXQobGluZSwgJzonLCAxKSwgbmFtZSA9IF9hWzBdLCB2YWx1ZSA9IF9hWzFdO1xuICAgIHJldHVybiB7XG4gICAgICAgIG5hbWU6IG5hbWUsXG4gICAgICAgIHZhbHVlOiB2YWx1ZSxcbiAgICB9O1xufVxuZnVuY3Rpb24gYnJlYWtEb3duTGluZShsaW5lKSB7XG4gICAgdmFyIF9hID0gZXh0cmFjdE5hbWUobGluZSksIG5hbWUgPSBfYS5uYW1lLCB2YWx1ZSA9IF9hLnZhbHVlO1xuICAgIHZhciBwYXJtcyA9IG5hbWUuc3BsaXQoJzsnKTtcbiAgICBpZiAoIXBhcm1zKVxuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ2VtcHR5IHByb3BlcnR5IG5hbWUnKTtcbiAgICByZXR1cm4ge1xuICAgICAgICBuYW1lOiBwYXJtc1swXS50b1VwcGVyQ2FzZSgpLFxuICAgICAgICBwYXJtczogcGFybXMuc2xpY2UoMSksXG4gICAgICAgIHZhbHVlOiB2YWx1ZSxcbiAgICB9O1xufVxuZnVuY3Rpb24gc3BsaXRJbnRvTGluZXMocywgdW5mb2xkKSB7XG4gICAgaWYgKHVuZm9sZCA9PT0gdm9pZCAwKSB7IHVuZm9sZCA9IGZhbHNlOyB9XG4gICAgcyA9IHMgJiYgcy50cmltKCk7XG4gICAgaWYgKCFzKVxuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ0ludmFsaWQgZW1wdHkgc3RyaW5nJyk7XG4gICAgLy8gTW9yZSBpbmZvIGFib3V0ICd1bmZvbGQnIG9wdGlvblxuICAgIC8vIEdvIGhlYWQgdG8gaHR0cDovL3d3dy5pZXRmLm9yZy9yZmMvcmZjMjQ0NS50eHRcbiAgICBpZiAoIXVuZm9sZCkge1xuICAgICAgICByZXR1cm4gcy5zcGxpdCgvXFxzLyk7XG4gICAgfVxuICAgIHZhciBsaW5lcyA9IHMuc3BsaXQoJ1xcbicpO1xuICAgIHZhciBpID0gMDtcbiAgICB3aGlsZSAoaSA8IGxpbmVzLmxlbmd0aCkge1xuICAgICAgICAvLyBUT0RPXG4gICAgICAgIHZhciBsaW5lID0gKGxpbmVzW2ldID0gbGluZXNbaV0ucmVwbGFjZSgvXFxzKyQvZywgJycpKTtcbiAgICAgICAgaWYgKCFsaW5lKSB7XG4gICAgICAgICAgICBsaW5lcy5zcGxpY2UoaSwgMSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAoaSA+IDAgJiYgbGluZVswXSA9PT0gJyAnKSB7XG4gICAgICAgICAgICBsaW5lc1tpIC0gMV0gKz0gbGluZS5zbGljZSgxKTtcbiAgICAgICAgICAgIGxpbmVzLnNwbGljZShpLCAxKTtcbiAgICAgICAgfVxuICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgIGkgKz0gMTtcbiAgICAgICAgfVxuICAgIH1cbiAgICByZXR1cm4gbGluZXM7XG59XG5mdW5jdGlvbiB2YWxpZGF0ZURhdGVQYXJtKHBhcm1zKSB7XG4gICAgcGFybXMuZm9yRWFjaChmdW5jdGlvbiAocGFybSkge1xuICAgICAgICBpZiAoIS8oVkFMVUU9REFURSgtVElNRSk/KXwoVFpJRD0pLy50ZXN0KHBhcm0pKSB7XG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ3Vuc3VwcG9ydGVkIFJEQVRFL0VYREFURSBwYXJtOiAnICsgcGFybSk7XG4gICAgICAgIH1cbiAgICB9KTtcbn1cbmZ1bmN0aW9uIHBhcnNlUkRhdGUocmRhdGV2YWwsIHBhcm1zKSB7XG4gICAgdmFsaWRhdGVEYXRlUGFybShwYXJtcyk7XG4gICAgcmV0dXJuIHJkYXRldmFsXG4gICAgICAgIC5zcGxpdCgnLCcpXG4gICAgICAgIC5tYXAoZnVuY3Rpb24gKGRhdGVzdHIpIHsgcmV0dXJuIGRhdGV1dGlsLnVudGlsU3RyaW5nVG9EYXRlKGRhdGVzdHIpOyB9KTtcbn1cbi8vIyBzb3VyY2VNYXBwaW5nVVJMPXJydWxlc3RyLmpzLm1hcCIsImV4cG9ydCB2YXIgRnJlcXVlbmN5O1xuKGZ1bmN0aW9uIChGcmVxdWVuY3kpIHtcbiAgICBGcmVxdWVuY3lbRnJlcXVlbmN5W1wiWUVBUkxZXCJdID0gMF0gPSBcIllFQVJMWVwiO1xuICAgIEZyZXF1ZW5jeVtGcmVxdWVuY3lbXCJNT05USExZXCJdID0gMV0gPSBcIk1PTlRITFlcIjtcbiAgICBGcmVxdWVuY3lbRnJlcXVlbmN5W1wiV0VFS0xZXCJdID0gMl0gPSBcIldFRUtMWVwiO1xuICAgIEZyZXF1ZW5jeVtGcmVxdWVuY3lbXCJEQUlMWVwiXSA9IDNdID0gXCJEQUlMWVwiO1xuICAgIEZyZXF1ZW5jeVtGcmVxdWVuY3lbXCJIT1VSTFlcIl0gPSA0XSA9IFwiSE9VUkxZXCI7XG4gICAgRnJlcXVlbmN5W0ZyZXF1ZW5jeVtcIk1JTlVURUxZXCJdID0gNV0gPSBcIk1JTlVURUxZXCI7XG4gICAgRnJlcXVlbmN5W0ZyZXF1ZW5jeVtcIlNFQ09ORExZXCJdID0gNl0gPSBcIlNFQ09ORExZXCI7XG59KShGcmVxdWVuY3kgfHwgKEZyZXF1ZW5jeSA9IHt9KSk7XG5leHBvcnQgZnVuY3Rpb24gZnJlcUlzRGFpbHlPckdyZWF0ZXIoZnJlcSkge1xuICAgIHJldHVybiBmcmVxIDwgRnJlcXVlbmN5LkhPVVJMWTtcbn1cbi8vIyBzb3VyY2VNYXBwaW5nVVJMPXR5cGVzLmpzLm1hcCIsIi8vID09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09XG4vLyBXZWVrZGF5XG4vLyA9PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PVxuZXhwb3J0IHZhciBBTExfV0VFS0RBWVMgPSBbXG4gICAgJ01PJyxcbiAgICAnVFUnLFxuICAgICdXRScsXG4gICAgJ1RIJyxcbiAgICAnRlInLFxuICAgICdTQScsXG4gICAgJ1NVJyxcbl07XG52YXIgV2Vla2RheSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBXZWVrZGF5KHdlZWtkYXksIG4pIHtcbiAgICAgICAgaWYgKG4gPT09IDApXG4gICAgICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJDYW4ndCBjcmVhdGUgd2Vla2RheSB3aXRoIG4gPT0gMFwiKTtcbiAgICAgICAgdGhpcy53ZWVrZGF5ID0gd2Vla2RheTtcbiAgICAgICAgdGhpcy5uID0gbjtcbiAgICB9XG4gICAgV2Vla2RheS5mcm9tU3RyID0gZnVuY3Rpb24gKHN0cikge1xuICAgICAgICByZXR1cm4gbmV3IFdlZWtkYXkoQUxMX1dFRUtEQVlTLmluZGV4T2Yoc3RyKSk7XG4gICAgfTtcbiAgICAvLyBfX2NhbGxfXyAtIENhbm5vdCBjYWxsIHRoZSBvYmplY3QgZGlyZWN0bHksIGRvIGl0IHRocm91Z2hcbiAgICAvLyBlLmcuIFJSdWxlLlRILm50aCgtMSkgaW5zdGVhZCxcbiAgICBXZWVrZGF5LnByb3RvdHlwZS5udGggPSBmdW5jdGlvbiAobikge1xuICAgICAgICByZXR1cm4gdGhpcy5uID09PSBuID8gdGhpcyA6IG5ldyBXZWVrZGF5KHRoaXMud2Vla2RheSwgbik7XG4gICAgfTtcbiAgICAvLyBfX2VxX19cbiAgICBXZWVrZGF5LnByb3RvdHlwZS5lcXVhbHMgPSBmdW5jdGlvbiAob3RoZXIpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMud2Vla2RheSA9PT0gb3RoZXIud2Vla2RheSAmJiB0aGlzLm4gPT09IG90aGVyLm47XG4gICAgfTtcbiAgICAvLyBfX3JlcHJfX1xuICAgIFdlZWtkYXkucHJvdG90eXBlLnRvU3RyaW5nID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgcyA9IEFMTF9XRUVLREFZU1t0aGlzLndlZWtkYXldO1xuICAgICAgICBpZiAodGhpcy5uKVxuICAgICAgICAgICAgcyA9ICh0aGlzLm4gPiAwID8gJysnIDogJycpICsgU3RyaW5nKHRoaXMubikgKyBzO1xuICAgICAgICByZXR1cm4gcztcbiAgICB9O1xuICAgIFdlZWtkYXkucHJvdG90eXBlLmdldEpzV2Vla2RheSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIHRoaXMud2Vla2RheSA9PT0gNiA/IDAgOiB0aGlzLndlZWtkYXkgKyAxO1xuICAgIH07XG4gICAgcmV0dXJuIFdlZWtkYXk7XG59KCkpO1xuZXhwb3J0IHsgV2Vla2RheSB9O1xuLy8jIHNvdXJjZU1hcHBpbmdVUkw9d2Vla2RheS5qcy5tYXAiLCIvKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqXHJcbkNvcHlyaWdodCAoYykgTWljcm9zb2Z0IENvcnBvcmF0aW9uLlxyXG5cclxuUGVybWlzc2lvbiB0byB1c2UsIGNvcHksIG1vZGlmeSwgYW5kL29yIGRpc3RyaWJ1dGUgdGhpcyBzb2Z0d2FyZSBmb3IgYW55XHJcbnB1cnBvc2Ugd2l0aCBvciB3aXRob3V0IGZlZSBpcyBoZXJlYnkgZ3JhbnRlZC5cclxuXHJcblRIRSBTT0ZUV0FSRSBJUyBQUk9WSURFRCBcIkFTIElTXCIgQU5EIFRIRSBBVVRIT1IgRElTQ0xBSU1TIEFMTCBXQVJSQU5USUVTIFdJVEhcclxuUkVHQVJEIFRPIFRISVMgU09GVFdBUkUgSU5DTFVESU5HIEFMTCBJTVBMSUVEIFdBUlJBTlRJRVMgT0YgTUVSQ0hBTlRBQklMSVRZXHJcbkFORCBGSVRORVNTLiBJTiBOTyBFVkVOVCBTSEFMTCBUSEUgQVVUSE9SIEJFIExJQUJMRSBGT1IgQU5ZIFNQRUNJQUwsIERJUkVDVCxcclxuSU5ESVJFQ1QsIE9SIENPTlNFUVVFTlRJQUwgREFNQUdFUyBPUiBBTlkgREFNQUdFUyBXSEFUU09FVkVSIFJFU1VMVElORyBGUk9NXHJcbkxPU1MgT0YgVVNFLCBEQVRBIE9SIFBST0ZJVFMsIFdIRVRIRVIgSU4gQU4gQUNUSU9OIE9GIENPTlRSQUNULCBORUdMSUdFTkNFIE9SXHJcbk9USEVSIFRPUlRJT1VTIEFDVElPTiwgQVJJU0lORyBPVVQgT0YgT1IgSU4gQ09OTkVDVElPTiBXSVRIIFRIRSBVU0UgT1JcclxuUEVSRk9STUFOQ0UgT0YgVEhJUyBTT0ZUV0FSRS5cclxuKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiogKi9cclxuLyogZ2xvYmFsIFJlZmxlY3QsIFByb21pc2UgKi9cclxuXHJcbnZhciBleHRlbmRTdGF0aWNzID0gZnVuY3Rpb24oZCwgYikge1xyXG4gICAgZXh0ZW5kU3RhdGljcyA9IE9iamVjdC5zZXRQcm90b3R5cGVPZiB8fFxyXG4gICAgICAgICh7IF9fcHJvdG9fXzogW10gfSBpbnN0YW5jZW9mIEFycmF5ICYmIGZ1bmN0aW9uIChkLCBiKSB7IGQuX19wcm90b19fID0gYjsgfSkgfHxcclxuICAgICAgICBmdW5jdGlvbiAoZCwgYikgeyBmb3IgKHZhciBwIGluIGIpIGlmIChPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwoYiwgcCkpIGRbcF0gPSBiW3BdOyB9O1xyXG4gICAgcmV0dXJuIGV4dGVuZFN0YXRpY3MoZCwgYik7XHJcbn07XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19leHRlbmRzKGQsIGIpIHtcclxuICAgIGlmICh0eXBlb2YgYiAhPT0gXCJmdW5jdGlvblwiICYmIGIgIT09IG51bGwpXHJcbiAgICAgICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNsYXNzIGV4dGVuZHMgdmFsdWUgXCIgKyBTdHJpbmcoYikgKyBcIiBpcyBub3QgYSBjb25zdHJ1Y3RvciBvciBudWxsXCIpO1xyXG4gICAgZXh0ZW5kU3RhdGljcyhkLCBiKTtcclxuICAgIGZ1bmN0aW9uIF9fKCkgeyB0aGlzLmNvbnN0cnVjdG9yID0gZDsgfVxyXG4gICAgZC5wcm90b3R5cGUgPSBiID09PSBudWxsID8gT2JqZWN0LmNyZWF0ZShiKSA6IChfXy5wcm90b3R5cGUgPSBiLnByb3RvdHlwZSwgbmV3IF9fKCkpO1xyXG59XHJcblxyXG5leHBvcnQgdmFyIF9fYXNzaWduID0gZnVuY3Rpb24oKSB7XHJcbiAgICBfX2Fzc2lnbiA9IE9iamVjdC5hc3NpZ24gfHwgZnVuY3Rpb24gX19hc3NpZ24odCkge1xyXG4gICAgICAgIGZvciAodmFyIHMsIGkgPSAxLCBuID0gYXJndW1lbnRzLmxlbmd0aDsgaSA8IG47IGkrKykge1xyXG4gICAgICAgICAgICBzID0gYXJndW1lbnRzW2ldO1xyXG4gICAgICAgICAgICBmb3IgKHZhciBwIGluIHMpIGlmIChPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwocywgcCkpIHRbcF0gPSBzW3BdO1xyXG4gICAgICAgIH1cclxuICAgICAgICByZXR1cm4gdDtcclxuICAgIH1cclxuICAgIHJldHVybiBfX2Fzc2lnbi5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19yZXN0KHMsIGUpIHtcclxuICAgIHZhciB0ID0ge307XHJcbiAgICBmb3IgKHZhciBwIGluIHMpIGlmIChPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwocywgcCkgJiYgZS5pbmRleE9mKHApIDwgMClcclxuICAgICAgICB0W3BdID0gc1twXTtcclxuICAgIGlmIChzICE9IG51bGwgJiYgdHlwZW9mIE9iamVjdC5nZXRPd25Qcm9wZXJ0eVN5bWJvbHMgPT09IFwiZnVuY3Rpb25cIilcclxuICAgICAgICBmb3IgKHZhciBpID0gMCwgcCA9IE9iamVjdC5nZXRPd25Qcm9wZXJ0eVN5bWJvbHMocyk7IGkgPCBwLmxlbmd0aDsgaSsrKSB7XHJcbiAgICAgICAgICAgIGlmIChlLmluZGV4T2YocFtpXSkgPCAwICYmIE9iamVjdC5wcm90b3R5cGUucHJvcGVydHlJc0VudW1lcmFibGUuY2FsbChzLCBwW2ldKSlcclxuICAgICAgICAgICAgICAgIHRbcFtpXV0gPSBzW3BbaV1dO1xyXG4gICAgICAgIH1cclxuICAgIHJldHVybiB0O1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19kZWNvcmF0ZShkZWNvcmF0b3JzLCB0YXJnZXQsIGtleSwgZGVzYykge1xyXG4gICAgdmFyIGMgPSBhcmd1bWVudHMubGVuZ3RoLCByID0gYyA8IDMgPyB0YXJnZXQgOiBkZXNjID09PSBudWxsID8gZGVzYyA9IE9iamVjdC5nZXRPd25Qcm9wZXJ0eURlc2NyaXB0b3IodGFyZ2V0LCBrZXkpIDogZGVzYywgZDtcclxuICAgIGlmICh0eXBlb2YgUmVmbGVjdCA9PT0gXCJvYmplY3RcIiAmJiB0eXBlb2YgUmVmbGVjdC5kZWNvcmF0ZSA9PT0gXCJmdW5jdGlvblwiKSByID0gUmVmbGVjdC5kZWNvcmF0ZShkZWNvcmF0b3JzLCB0YXJnZXQsIGtleSwgZGVzYyk7XHJcbiAgICBlbHNlIGZvciAodmFyIGkgPSBkZWNvcmF0b3JzLmxlbmd0aCAtIDE7IGkgPj0gMDsgaS0tKSBpZiAoZCA9IGRlY29yYXRvcnNbaV0pIHIgPSAoYyA8IDMgPyBkKHIpIDogYyA+IDMgPyBkKHRhcmdldCwga2V5LCByKSA6IGQodGFyZ2V0LCBrZXkpKSB8fCByO1xyXG4gICAgcmV0dXJuIGMgPiAzICYmIHIgJiYgT2JqZWN0LmRlZmluZVByb3BlcnR5KHRhcmdldCwga2V5LCByKSwgcjtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fcGFyYW0ocGFyYW1JbmRleCwgZGVjb3JhdG9yKSB7XHJcbiAgICByZXR1cm4gZnVuY3Rpb24gKHRhcmdldCwga2V5KSB7IGRlY29yYXRvcih0YXJnZXQsIGtleSwgcGFyYW1JbmRleCk7IH1cclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fbWV0YWRhdGEobWV0YWRhdGFLZXksIG1ldGFkYXRhVmFsdWUpIHtcclxuICAgIGlmICh0eXBlb2YgUmVmbGVjdCA9PT0gXCJvYmplY3RcIiAmJiB0eXBlb2YgUmVmbGVjdC5tZXRhZGF0YSA9PT0gXCJmdW5jdGlvblwiKSByZXR1cm4gUmVmbGVjdC5tZXRhZGF0YShtZXRhZGF0YUtleSwgbWV0YWRhdGFWYWx1ZSk7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2F3YWl0ZXIodGhpc0FyZywgX2FyZ3VtZW50cywgUCwgZ2VuZXJhdG9yKSB7XHJcbiAgICBmdW5jdGlvbiBhZG9wdCh2YWx1ZSkgeyByZXR1cm4gdmFsdWUgaW5zdGFuY2VvZiBQID8gdmFsdWUgOiBuZXcgUChmdW5jdGlvbiAocmVzb2x2ZSkgeyByZXNvbHZlKHZhbHVlKTsgfSk7IH1cclxuICAgIHJldHVybiBuZXcgKFAgfHwgKFAgPSBQcm9taXNlKSkoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgIGZ1bmN0aW9uIGZ1bGZpbGxlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvci5uZXh0KHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiByZWplY3RlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvcltcInRocm93XCJdKHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiBzdGVwKHJlc3VsdCkgeyByZXN1bHQuZG9uZSA/IHJlc29sdmUocmVzdWx0LnZhbHVlKSA6IGFkb3B0KHJlc3VsdC52YWx1ZSkudGhlbihmdWxmaWxsZWQsIHJlamVjdGVkKTsgfVxyXG4gICAgICAgIHN0ZXAoKGdlbmVyYXRvciA9IGdlbmVyYXRvci5hcHBseSh0aGlzQXJnLCBfYXJndW1lbnRzIHx8IFtdKSkubmV4dCgpKTtcclxuICAgIH0pO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19nZW5lcmF0b3IodGhpc0FyZywgYm9keSkge1xyXG4gICAgdmFyIF8gPSB7IGxhYmVsOiAwLCBzZW50OiBmdW5jdGlvbigpIHsgaWYgKHRbMF0gJiAxKSB0aHJvdyB0WzFdOyByZXR1cm4gdFsxXTsgfSwgdHJ5czogW10sIG9wczogW10gfSwgZiwgeSwgdCwgZztcclxuICAgIHJldHVybiBnID0geyBuZXh0OiB2ZXJiKDApLCBcInRocm93XCI6IHZlcmIoMSksIFwicmV0dXJuXCI6IHZlcmIoMikgfSwgdHlwZW9mIFN5bWJvbCA9PT0gXCJmdW5jdGlvblwiICYmIChnW1N5bWJvbC5pdGVyYXRvcl0gPSBmdW5jdGlvbigpIHsgcmV0dXJuIHRoaXM7IH0pLCBnO1xyXG4gICAgZnVuY3Rpb24gdmVyYihuKSB7IHJldHVybiBmdW5jdGlvbiAodikgeyByZXR1cm4gc3RlcChbbiwgdl0pOyB9OyB9XHJcbiAgICBmdW5jdGlvbiBzdGVwKG9wKSB7XHJcbiAgICAgICAgaWYgKGYpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJHZW5lcmF0b3IgaXMgYWxyZWFkeSBleGVjdXRpbmcuXCIpO1xyXG4gICAgICAgIHdoaWxlIChfKSB0cnkge1xyXG4gICAgICAgICAgICBpZiAoZiA9IDEsIHkgJiYgKHQgPSBvcFswXSAmIDIgPyB5W1wicmV0dXJuXCJdIDogb3BbMF0gPyB5W1widGhyb3dcIl0gfHwgKCh0ID0geVtcInJldHVyblwiXSkgJiYgdC5jYWxsKHkpLCAwKSA6IHkubmV4dCkgJiYgISh0ID0gdC5jYWxsKHksIG9wWzFdKSkuZG9uZSkgcmV0dXJuIHQ7XHJcbiAgICAgICAgICAgIGlmICh5ID0gMCwgdCkgb3AgPSBbb3BbMF0gJiAyLCB0LnZhbHVlXTtcclxuICAgICAgICAgICAgc3dpdGNoIChvcFswXSkge1xyXG4gICAgICAgICAgICAgICAgY2FzZSAwOiBjYXNlIDE6IHQgPSBvcDsgYnJlYWs7XHJcbiAgICAgICAgICAgICAgICBjYXNlIDQ6IF8ubGFiZWwrKzsgcmV0dXJuIHsgdmFsdWU6IG9wWzFdLCBkb25lOiBmYWxzZSB9O1xyXG4gICAgICAgICAgICAgICAgY2FzZSA1OiBfLmxhYmVsKys7IHkgPSBvcFsxXTsgb3AgPSBbMF07IGNvbnRpbnVlO1xyXG4gICAgICAgICAgICAgICAgY2FzZSA3OiBvcCA9IF8ub3BzLnBvcCgpOyBfLnRyeXMucG9wKCk7IGNvbnRpbnVlO1xyXG4gICAgICAgICAgICAgICAgZGVmYXVsdDpcclxuICAgICAgICAgICAgICAgICAgICBpZiAoISh0ID0gXy50cnlzLCB0ID0gdC5sZW5ndGggPiAwICYmIHRbdC5sZW5ndGggLSAxXSkgJiYgKG9wWzBdID09PSA2IHx8IG9wWzBdID09PSAyKSkgeyBfID0gMDsgY29udGludWU7IH1cclxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDMgJiYgKCF0IHx8IChvcFsxXSA+IHRbMF0gJiYgb3BbMV0gPCB0WzNdKSkpIHsgXy5sYWJlbCA9IG9wWzFdOyBicmVhazsgfVxyXG4gICAgICAgICAgICAgICAgICAgIGlmIChvcFswXSA9PT0gNiAmJiBfLmxhYmVsIDwgdFsxXSkgeyBfLmxhYmVsID0gdFsxXTsgdCA9IG9wOyBicmVhazsgfVxyXG4gICAgICAgICAgICAgICAgICAgIGlmICh0ICYmIF8ubGFiZWwgPCB0WzJdKSB7IF8ubGFiZWwgPSB0WzJdOyBfLm9wcy5wdXNoKG9wKTsgYnJlYWs7IH1cclxuICAgICAgICAgICAgICAgICAgICBpZiAodFsyXSkgXy5vcHMucG9wKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgXy50cnlzLnBvcCgpOyBjb250aW51ZTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICBvcCA9IGJvZHkuY2FsbCh0aGlzQXJnLCBfKTtcclxuICAgICAgICB9IGNhdGNoIChlKSB7IG9wID0gWzYsIGVdOyB5ID0gMDsgfSBmaW5hbGx5IHsgZiA9IHQgPSAwOyB9XHJcbiAgICAgICAgaWYgKG9wWzBdICYgNSkgdGhyb3cgb3BbMV07IHJldHVybiB7IHZhbHVlOiBvcFswXSA/IG9wWzFdIDogdm9pZCAwLCBkb25lOiB0cnVlIH07XHJcbiAgICB9XHJcbn1cclxuXHJcbmV4cG9ydCB2YXIgX19jcmVhdGVCaW5kaW5nID0gT2JqZWN0LmNyZWF0ZSA/IChmdW5jdGlvbihvLCBtLCBrLCBrMikge1xyXG4gICAgaWYgKGsyID09PSB1bmRlZmluZWQpIGsyID0gaztcclxuICAgIHZhciBkZXNjID0gT2JqZWN0LmdldE93blByb3BlcnR5RGVzY3JpcHRvcihtLCBrKTtcclxuICAgIGlmICghZGVzYyB8fCAoXCJnZXRcIiBpbiBkZXNjID8gIW0uX19lc01vZHVsZSA6IGRlc2Mud3JpdGFibGUgfHwgZGVzYy5jb25maWd1cmFibGUpKSB7XHJcbiAgICAgICAgZGVzYyA9IHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbigpIHsgcmV0dXJuIG1ba107IH0gfTtcclxuICAgIH1cclxuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvLCBrMiwgZGVzYyk7XHJcbn0pIDogKGZ1bmN0aW9uKG8sIG0sIGssIGsyKSB7XHJcbiAgICBpZiAoazIgPT09IHVuZGVmaW5lZCkgazIgPSBrO1xyXG4gICAgb1trMl0gPSBtW2tdO1xyXG59KTtcclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2V4cG9ydFN0YXIobSwgbykge1xyXG4gICAgZm9yICh2YXIgcCBpbiBtKSBpZiAocCAhPT0gXCJkZWZhdWx0XCIgJiYgIU9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbChvLCBwKSkgX19jcmVhdGVCaW5kaW5nKG8sIG0sIHApO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX192YWx1ZXMobykge1xyXG4gICAgdmFyIHMgPSB0eXBlb2YgU3ltYm9sID09PSBcImZ1bmN0aW9uXCIgJiYgU3ltYm9sLml0ZXJhdG9yLCBtID0gcyAmJiBvW3NdLCBpID0gMDtcclxuICAgIGlmIChtKSByZXR1cm4gbS5jYWxsKG8pO1xyXG4gICAgaWYgKG8gJiYgdHlwZW9mIG8ubGVuZ3RoID09PSBcIm51bWJlclwiKSByZXR1cm4ge1xyXG4gICAgICAgIG5leHQ6IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICAgICAgaWYgKG8gJiYgaSA+PSBvLmxlbmd0aCkgbyA9IHZvaWQgMDtcclxuICAgICAgICAgICAgcmV0dXJuIHsgdmFsdWU6IG8gJiYgb1tpKytdLCBkb25lOiAhbyB9O1xyXG4gICAgICAgIH1cclxuICAgIH07XHJcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKHMgPyBcIk9iamVjdCBpcyBub3QgaXRlcmFibGUuXCIgOiBcIlN5bWJvbC5pdGVyYXRvciBpcyBub3QgZGVmaW5lZC5cIik7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX3JlYWQobywgbikge1xyXG4gICAgdmFyIG0gPSB0eXBlb2YgU3ltYm9sID09PSBcImZ1bmN0aW9uXCIgJiYgb1tTeW1ib2wuaXRlcmF0b3JdO1xyXG4gICAgaWYgKCFtKSByZXR1cm4gbztcclxuICAgIHZhciBpID0gbS5jYWxsKG8pLCByLCBhciA9IFtdLCBlO1xyXG4gICAgdHJ5IHtcclxuICAgICAgICB3aGlsZSAoKG4gPT09IHZvaWQgMCB8fCBuLS0gPiAwKSAmJiAhKHIgPSBpLm5leHQoKSkuZG9uZSkgYXIucHVzaChyLnZhbHVlKTtcclxuICAgIH1cclxuICAgIGNhdGNoIChlcnJvcikgeyBlID0geyBlcnJvcjogZXJyb3IgfTsgfVxyXG4gICAgZmluYWxseSB7XHJcbiAgICAgICAgdHJ5IHtcclxuICAgICAgICAgICAgaWYgKHIgJiYgIXIuZG9uZSAmJiAobSA9IGlbXCJyZXR1cm5cIl0pKSBtLmNhbGwoaSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGZpbmFsbHkgeyBpZiAoZSkgdGhyb3cgZS5lcnJvcjsgfVxyXG4gICAgfVxyXG4gICAgcmV0dXJuIGFyO1xyXG59XHJcblxyXG4vKiogQGRlcHJlY2F0ZWQgKi9cclxuZXhwb3J0IGZ1bmN0aW9uIF9fc3ByZWFkKCkge1xyXG4gICAgZm9yICh2YXIgYXIgPSBbXSwgaSA9IDA7IGkgPCBhcmd1bWVudHMubGVuZ3RoOyBpKyspXHJcbiAgICAgICAgYXIgPSBhci5jb25jYXQoX19yZWFkKGFyZ3VtZW50c1tpXSkpO1xyXG4gICAgcmV0dXJuIGFyO1xyXG59XHJcblxyXG4vKiogQGRlcHJlY2F0ZWQgKi9cclxuZXhwb3J0IGZ1bmN0aW9uIF9fc3ByZWFkQXJyYXlzKCkge1xyXG4gICAgZm9yICh2YXIgcyA9IDAsIGkgPSAwLCBpbCA9IGFyZ3VtZW50cy5sZW5ndGg7IGkgPCBpbDsgaSsrKSBzICs9IGFyZ3VtZW50c1tpXS5sZW5ndGg7XHJcbiAgICBmb3IgKHZhciByID0gQXJyYXkocyksIGsgPSAwLCBpID0gMDsgaSA8IGlsOyBpKyspXHJcbiAgICAgICAgZm9yICh2YXIgYSA9IGFyZ3VtZW50c1tpXSwgaiA9IDAsIGpsID0gYS5sZW5ndGg7IGogPCBqbDsgaisrLCBrKyspXHJcbiAgICAgICAgICAgIHJba10gPSBhW2pdO1xyXG4gICAgcmV0dXJuIHI7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX3NwcmVhZEFycmF5KHRvLCBmcm9tLCBwYWNrKSB7XHJcbiAgICBpZiAocGFjayB8fCBhcmd1bWVudHMubGVuZ3RoID09PSAyKSBmb3IgKHZhciBpID0gMCwgbCA9IGZyb20ubGVuZ3RoLCBhcjsgaSA8IGw7IGkrKykge1xyXG4gICAgICAgIGlmIChhciB8fCAhKGkgaW4gZnJvbSkpIHtcclxuICAgICAgICAgICAgaWYgKCFhcikgYXIgPSBBcnJheS5wcm90b3R5cGUuc2xpY2UuY2FsbChmcm9tLCAwLCBpKTtcclxuICAgICAgICAgICAgYXJbaV0gPSBmcm9tW2ldO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIHJldHVybiB0by5jb25jYXQoYXIgfHwgQXJyYXkucHJvdG90eXBlLnNsaWNlLmNhbGwoZnJvbSkpO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19hd2FpdCh2KSB7XHJcbiAgICByZXR1cm4gdGhpcyBpbnN0YW5jZW9mIF9fYXdhaXQgPyAodGhpcy52ID0gdiwgdGhpcykgOiBuZXcgX19hd2FpdCh2KTtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fYXN5bmNHZW5lcmF0b3IodGhpc0FyZywgX2FyZ3VtZW50cywgZ2VuZXJhdG9yKSB7XHJcbiAgICBpZiAoIVN5bWJvbC5hc3luY0l0ZXJhdG9yKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiU3ltYm9sLmFzeW5jSXRlcmF0b3IgaXMgbm90IGRlZmluZWQuXCIpO1xyXG4gICAgdmFyIGcgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSksIGksIHEgPSBbXTtcclxuICAgIHJldHVybiBpID0ge30sIHZlcmIoXCJuZXh0XCIpLCB2ZXJiKFwidGhyb3dcIiksIHZlcmIoXCJyZXR1cm5cIiksIGlbU3ltYm9sLmFzeW5jSXRlcmF0b3JdID0gZnVuY3Rpb24gKCkgeyByZXR1cm4gdGhpczsgfSwgaTtcclxuICAgIGZ1bmN0aW9uIHZlcmIobikgeyBpZiAoZ1tuXSkgaVtuXSA9IGZ1bmN0aW9uICh2KSB7IHJldHVybiBuZXcgUHJvbWlzZShmdW5jdGlvbiAoYSwgYikgeyBxLnB1c2goW24sIHYsIGEsIGJdKSA+IDEgfHwgcmVzdW1lKG4sIHYpOyB9KTsgfTsgfVxyXG4gICAgZnVuY3Rpb24gcmVzdW1lKG4sIHYpIHsgdHJ5IHsgc3RlcChnW25dKHYpKTsgfSBjYXRjaCAoZSkgeyBzZXR0bGUocVswXVszXSwgZSk7IH0gfVxyXG4gICAgZnVuY3Rpb24gc3RlcChyKSB7IHIudmFsdWUgaW5zdGFuY2VvZiBfX2F3YWl0ID8gUHJvbWlzZS5yZXNvbHZlKHIudmFsdWUudikudGhlbihmdWxmaWxsLCByZWplY3QpIDogc2V0dGxlKHFbMF1bMl0sIHIpOyB9XHJcbiAgICBmdW5jdGlvbiBmdWxmaWxsKHZhbHVlKSB7IHJlc3VtZShcIm5leHRcIiwgdmFsdWUpOyB9XHJcbiAgICBmdW5jdGlvbiByZWplY3QodmFsdWUpIHsgcmVzdW1lKFwidGhyb3dcIiwgdmFsdWUpOyB9XHJcbiAgICBmdW5jdGlvbiBzZXR0bGUoZiwgdikgeyBpZiAoZih2KSwgcS5zaGlmdCgpLCBxLmxlbmd0aCkgcmVzdW1lKHFbMF1bMF0sIHFbMF1bMV0pOyB9XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2FzeW5jRGVsZWdhdG9yKG8pIHtcclxuICAgIHZhciBpLCBwO1xyXG4gICAgcmV0dXJuIGkgPSB7fSwgdmVyYihcIm5leHRcIiksIHZlcmIoXCJ0aHJvd1wiLCBmdW5jdGlvbiAoZSkgeyB0aHJvdyBlOyB9KSwgdmVyYihcInJldHVyblwiKSwgaVtTeW1ib2wuaXRlcmF0b3JdID0gZnVuY3Rpb24gKCkgeyByZXR1cm4gdGhpczsgfSwgaTtcclxuICAgIGZ1bmN0aW9uIHZlcmIobiwgZikgeyBpW25dID0gb1tuXSA/IGZ1bmN0aW9uICh2KSB7IHJldHVybiAocCA9ICFwKSA/IHsgdmFsdWU6IF9fYXdhaXQob1tuXSh2KSksIGRvbmU6IG4gPT09IFwicmV0dXJuXCIgfSA6IGYgPyBmKHYpIDogdjsgfSA6IGY7IH1cclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fYXN5bmNWYWx1ZXMobykge1xyXG4gICAgaWYgKCFTeW1ib2wuYXN5bmNJdGVyYXRvcikgdGhyb3cgbmV3IFR5cGVFcnJvcihcIlN5bWJvbC5hc3luY0l0ZXJhdG9yIGlzIG5vdCBkZWZpbmVkLlwiKTtcclxuICAgIHZhciBtID0gb1tTeW1ib2wuYXN5bmNJdGVyYXRvcl0sIGk7XHJcbiAgICByZXR1cm4gbSA/IG0uY2FsbChvKSA6IChvID0gdHlwZW9mIF9fdmFsdWVzID09PSBcImZ1bmN0aW9uXCIgPyBfX3ZhbHVlcyhvKSA6IG9bU3ltYm9sLml0ZXJhdG9yXSgpLCBpID0ge30sIHZlcmIoXCJuZXh0XCIpLCB2ZXJiKFwidGhyb3dcIiksIHZlcmIoXCJyZXR1cm5cIiksIGlbU3ltYm9sLmFzeW5jSXRlcmF0b3JdID0gZnVuY3Rpb24gKCkgeyByZXR1cm4gdGhpczsgfSwgaSk7XHJcbiAgICBmdW5jdGlvbiB2ZXJiKG4pIHsgaVtuXSA9IG9bbl0gJiYgZnVuY3Rpb24gKHYpIHsgcmV0dXJuIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHsgdiA9IG9bbl0odiksIHNldHRsZShyZXNvbHZlLCByZWplY3QsIHYuZG9uZSwgdi52YWx1ZSk7IH0pOyB9OyB9XHJcbiAgICBmdW5jdGlvbiBzZXR0bGUocmVzb2x2ZSwgcmVqZWN0LCBkLCB2KSB7IFByb21pc2UucmVzb2x2ZSh2KS50aGVuKGZ1bmN0aW9uKHYpIHsgcmVzb2x2ZSh7IHZhbHVlOiB2LCBkb25lOiBkIH0pOyB9LCByZWplY3QpOyB9XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX21ha2VUZW1wbGF0ZU9iamVjdChjb29rZWQsIHJhdykge1xyXG4gICAgaWYgKE9iamVjdC5kZWZpbmVQcm9wZXJ0eSkgeyBPYmplY3QuZGVmaW5lUHJvcGVydHkoY29va2VkLCBcInJhd1wiLCB7IHZhbHVlOiByYXcgfSk7IH0gZWxzZSB7IGNvb2tlZC5yYXcgPSByYXc7IH1cclxuICAgIHJldHVybiBjb29rZWQ7XHJcbn07XHJcblxyXG52YXIgX19zZXRNb2R1bGVEZWZhdWx0ID0gT2JqZWN0LmNyZWF0ZSA/IChmdW5jdGlvbihvLCB2KSB7XHJcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkobywgXCJkZWZhdWx0XCIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgdmFsdWU6IHYgfSk7XHJcbn0pIDogZnVuY3Rpb24obywgdikge1xyXG4gICAgb1tcImRlZmF1bHRcIl0gPSB2O1xyXG59O1xyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9faW1wb3J0U3Rhcihtb2QpIHtcclxuICAgIGlmIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpIHJldHVybiBtb2Q7XHJcbiAgICB2YXIgcmVzdWx0ID0ge307XHJcbiAgICBpZiAobW9kICE9IG51bGwpIGZvciAodmFyIGsgaW4gbW9kKSBpZiAoayAhPT0gXCJkZWZhdWx0XCIgJiYgT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG1vZCwgaykpIF9fY3JlYXRlQmluZGluZyhyZXN1bHQsIG1vZCwgayk7XHJcbiAgICBfX3NldE1vZHVsZURlZmF1bHQocmVzdWx0LCBtb2QpO1xyXG4gICAgcmV0dXJuIHJlc3VsdDtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9faW1wb3J0RGVmYXVsdChtb2QpIHtcclxuICAgIHJldHVybiAobW9kICYmIG1vZC5fX2VzTW9kdWxlKSA/IG1vZCA6IHsgZGVmYXVsdDogbW9kIH07XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2NsYXNzUHJpdmF0ZUZpZWxkR2V0KHJlY2VpdmVyLCBzdGF0ZSwga2luZCwgZikge1xyXG4gICAgaWYgKGtpbmQgPT09IFwiYVwiICYmICFmKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiUHJpdmF0ZSBhY2Nlc3NvciB3YXMgZGVmaW5lZCB3aXRob3V0IGEgZ2V0dGVyXCIpO1xyXG4gICAgaWYgKHR5cGVvZiBzdGF0ZSA9PT0gXCJmdW5jdGlvblwiID8gcmVjZWl2ZXIgIT09IHN0YXRlIHx8ICFmIDogIXN0YXRlLmhhcyhyZWNlaXZlcikpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgcmVhZCBwcml2YXRlIG1lbWJlciBmcm9tIGFuIG9iamVjdCB3aG9zZSBjbGFzcyBkaWQgbm90IGRlY2xhcmUgaXRcIik7XHJcbiAgICByZXR1cm4ga2luZCA9PT0gXCJtXCIgPyBmIDoga2luZCA9PT0gXCJhXCIgPyBmLmNhbGwocmVjZWl2ZXIpIDogZiA/IGYudmFsdWUgOiBzdGF0ZS5nZXQocmVjZWl2ZXIpO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19jbGFzc1ByaXZhdGVGaWVsZFNldChyZWNlaXZlciwgc3RhdGUsIHZhbHVlLCBraW5kLCBmKSB7XHJcbiAgICBpZiAoa2luZCA9PT0gXCJtXCIpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJQcml2YXRlIG1ldGhvZCBpcyBub3Qgd3JpdGFibGVcIik7XHJcbiAgICBpZiAoa2luZCA9PT0gXCJhXCIgJiYgIWYpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJQcml2YXRlIGFjY2Vzc29yIHdhcyBkZWZpbmVkIHdpdGhvdXQgYSBzZXR0ZXJcIik7XHJcbiAgICBpZiAodHlwZW9mIHN0YXRlID09PSBcImZ1bmN0aW9uXCIgPyByZWNlaXZlciAhPT0gc3RhdGUgfHwgIWYgOiAhc3RhdGUuaGFzKHJlY2VpdmVyKSkgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCB3cml0ZSBwcml2YXRlIG1lbWJlciB0byBhbiBvYmplY3Qgd2hvc2UgY2xhc3MgZGlkIG5vdCBkZWNsYXJlIGl0XCIpO1xyXG4gICAgcmV0dXJuIChraW5kID09PSBcImFcIiA/IGYuY2FsbChyZWNlaXZlciwgdmFsdWUpIDogZiA/IGYudmFsdWUgPSB2YWx1ZSA6IHN0YXRlLnNldChyZWNlaXZlciwgdmFsdWUpKSwgdmFsdWU7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2NsYXNzUHJpdmF0ZUZpZWxkSW4oc3RhdGUsIHJlY2VpdmVyKSB7XHJcbiAgICBpZiAocmVjZWl2ZXIgPT09IG51bGwgfHwgKHR5cGVvZiByZWNlaXZlciAhPT0gXCJvYmplY3RcIiAmJiB0eXBlb2YgcmVjZWl2ZXIgIT09IFwiZnVuY3Rpb25cIikpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgdXNlICdpbicgb3BlcmF0b3Igb24gbm9uLW9iamVjdFwiKTtcclxuICAgIHJldHVybiB0eXBlb2Ygc3RhdGUgPT09IFwiZnVuY3Rpb25cIiA/IHJlY2VpdmVyID09PSBzdGF0ZSA6IHN0YXRlLmhhcyhyZWNlaXZlcik7XHJcbn1cclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2F3YWl0ZXIgPSAodGhpcyAmJiB0aGlzLl9fYXdhaXRlcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIF9hcmd1bWVudHMsIFAsIGdlbmVyYXRvcikge1xyXG4gICAgZnVuY3Rpb24gYWRvcHQodmFsdWUpIHsgcmV0dXJuIHZhbHVlIGluc3RhbmNlb2YgUCA/IHZhbHVlIDogbmV3IFAoZnVuY3Rpb24gKHJlc29sdmUpIHsgcmVzb2x2ZSh2YWx1ZSk7IH0pOyB9XHJcbiAgICByZXR1cm4gbmV3IChQIHx8IChQID0gUHJvbWlzZSkpKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICBmdW5jdGlvbiBmdWxmaWxsZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3IubmV4dCh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gcmVqZWN0ZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3JbXCJ0aHJvd1wiXSh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gc3RlcChyZXN1bHQpIHsgcmVzdWx0LmRvbmUgPyByZXNvbHZlKHJlc3VsdC52YWx1ZSkgOiBhZG9wdChyZXN1bHQudmFsdWUpLnRoZW4oZnVsZmlsbGVkLCByZWplY3RlZCk7IH1cclxuICAgICAgICBzdGVwKChnZW5lcmF0b3IgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSkpLm5leHQoKSk7XHJcbiAgICB9KTtcclxufTtcclxudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XHJcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcclxufTtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBEb21haW5JdGVtXzEgPSBfX2ltcG9ydERlZmF1bHQocmVxdWlyZShcIkAvcGx1Z2luL2Ntcy9tb2RlbHMvRG9tYWluSXRlbVwiKSk7XHJcbmNvbnN0IEdhbWVQcm92aWRlcl8xID0gX19pbXBvcnREZWZhdWx0KHJlcXVpcmUoXCJAL3BsdWdpbi9jbXMvbW9kZWxzL0dhbWVQcm92aWRlclwiKSk7XHJcbmNvbnN0IFByb21vdGlvbl8xID0gcmVxdWlyZShcIkAvcGx1Z2luL3Byb21vdGlvbnMvUHJvbW90aW9uXCIpO1xyXG5jbGFzcyBGYWtlUHJvbUFwaSB7XHJcbiAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICB0aGlzLl9mYWtlRGIgPSBuZXcgTWFwKCk7XHJcbiAgICAgICAgdGhpcy5jcmVhdGVUZXN0RGF0YSgpO1xyXG4gICAgfVxyXG4gICAgZ2V0KCkge1xyXG4gICAgICAgIHJldHVybiBuZXcgUHJvbWlzZSgocmVzKSA9PiB7XHJcbiAgICAgICAgICAgIHNldFRpbWVvdXQoKCkgPT4ge1xyXG4gICAgICAgICAgICAgICAgcmVzKEFycmF5LmZyb20odGhpcy5fZmFrZURiLnZhbHVlcygpKSk7XHJcbiAgICAgICAgICAgIH0sIDUwMCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICBnZXRCeUlkKGtleSkge1xyXG4gICAgICAgIHJldHVybiBuZXcgUHJvbWlzZSgocmVzKSA9PiB7XHJcbiAgICAgICAgICAgIHNldFRpbWVvdXQoKCkgPT4ge1xyXG4gICAgICAgICAgICAgICAgY29uc3QgaXRlbSA9IHRoaXMuX2Zha2VEYi5nZXQoa2V5KTtcclxuICAgICAgICAgICAgICAgIHJlcyhpdGVtIHx8IG51bGwpO1xyXG4gICAgICAgICAgICB9LCA1MDApO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgZ2V0QmV0d2VlbihzdGFydERhdGUsIGVuZERhdGUpIHtcclxuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiogKCkge1xyXG4gICAgICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICAgICAgY29uc3Qgd2FudGVkUHJvbXMgPSBbXTtcclxuICAgICAgICAgICAgICAgIGZvciAoY29uc3QgW2tleSwgcHJvbW90aW9uXSBvZiB0aGlzLl9mYWtlRGIpIHtcclxuICAgICAgICAgICAgICAgICAgICBjb25zdCBydWxlID0gcHJvbW90aW9uLnNjaGVkdWxlLnNldERldGFpbHNCeVJydWxlKCk7XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKCFwcm9tb3Rpb24uc2NoZWR1bGUuZGF0ZVN0YXJ0IHx8ICFydWxlKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgICAgIGNvbnRpbnVlO1xyXG4gICAgICAgICAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgICAgICAgICBjb25zdCBldmVudHNCZXR3ZWVuRGF0ZXMgPSBydWxlLmJldHdlZW4oc3RhcnREYXRlLCBlbmREYXRlLCB0cnVlKTtcclxuICAgICAgICAgICAgICAgICAgICBjb25zdCBoYXNFdmVudHNCZXR3ZWVuID0gZXZlbnRzQmV0d2VlbkRhdGVzLmxlbmd0aCA+IDA7XHJcbiAgICAgICAgICAgICAgICAgICAgLy8gVE9ETzogVGhpcyBjYW4gYmUgYSBiaXQgbW9yZSBjb21wbGljYXRlZCBieSBjaGVja2luZyB0aGUgY291bnQgYW5kIHNlZWluZyBpZiBpdCdzIGVuZCBpc1xyXG4gICAgICAgICAgICAgICAgICAgIC8vIGJlZm9yZSB0aGlzIG1vbnRoXHJcbiAgICAgICAgICAgICAgICAgICAgLy8gVE9ETzogQ29uc2lkZXIgc2luZ2xlIGRheXNcclxuICAgICAgICAgICAgICAgICAgICBpZiAoaGFzRXZlbnRzQmV0d2Vlbikge1xyXG4gICAgICAgICAgICAgICAgICAgICAgICB3YW50ZWRQcm9tcy5wdXNoKHByb21vdGlvbik7XHJcbiAgICAgICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICAgICAgcmVzKHdhbnRlZFByb21zKTtcclxuICAgICAgICAgICAgICAgIH0sIDUwMCk7XHJcbiAgICAgICAgICAgIH0pO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgYWRkKGkpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIHRoaXMuX2Zha2VEYi5zZXQoaS5pZCwgaSk7XHJcbiAgICAgICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgICAgICByZXMoKTtcclxuICAgICAgICAgICAgICAgIH0sIDI1MCk7XHJcbiAgICAgICAgICAgIH0sIDI1MCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICB1cGRhdGUoaWQsIGkpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIGlmICh0aGlzLl9mYWtlRGIuaGFzKGlkKSkge1xyXG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX2Zha2VEYi5zZXQoaWQsIGkpO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICAgICAgcmVzKCk7XHJcbiAgICAgICAgICAgICAgICB9LCAyNTApO1xyXG4gICAgICAgICAgICB9LCAyNTApO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgY3JlYXRlVGVzdERhdGEoKSB7XHJcbiAgICAgICAgY29uc3QgZG9tYWluID0gbmV3IERvbWFpbkl0ZW1fMS5kZWZhdWx0KCdsaXZlc2NvcmVfdWsnLCAnbGl2ZXNjb3JlX3VrJywgdHJ1ZSk7XHJcbiAgICAgICAgY29uc3QgcHJvdmlkZXIgPSBuZXcgR2FtZVByb3ZpZGVyXzEuZGVmYXVsdCgncm94b3InLCBbXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLWNhc2luby1yb3hvcicpO1xyXG4gICAgICAgIGNvbnN0IHNpbmdsZSA9IG5ldyBQcm9tb3Rpb25fMS5Qcm9tb3Rpb24oKTtcclxuICAgICAgICBzaW5nbGUudGl0bGUgPSAnU2luZ2xlIEV2ZW50IFByb21vdGlvbic7XHJcbiAgICAgICAgc2luZ2xlLnNjaGVkdWxlLnJydWxlU3RyaW5nID0gJ1JSVUxFOkZSRVE9WUVBUkxZO0lOVEVSVkFMPTE7Q09VTlQ9MSc7XHJcbiAgICAgICAgY29uc3QgcmVjdXIgPSBuZXcgUHJvbW90aW9uXzEuUHJvbW90aW9uKCk7XHJcbiAgICAgICAgcmVjdXIudGl0bGUgPSAnUmVjdXJyaW5nIEV2ZW50IFByb21vdGlvbic7XHJcbiAgICAgICAgcmVjdXIuc2NoZWR1bGUucnJ1bGVTdHJpbmcgPSAnUlJVTEU6RlJFUT1XRUVLTFk7SU5URVJWQUw9MjtDT1VOVD0zJztcclxuICAgICAgICBzaW5nbGUuZG9tYWluID0gZG9tYWluO1xyXG4gICAgICAgIHJlY3VyLmRvbWFpbiA9IGRvbWFpbjtcclxuICAgICAgICB0aGlzLl9mYWtlRGIuc2V0KHNpbmdsZS5pZCwgc2luZ2xlKTtcclxuICAgICAgICB0aGlzLl9mYWtlRGIuc2V0KHJlY3VyLmlkLCByZWN1cik7XHJcbiAgICB9XHJcbn1cclxuY2xhc3MgUHJvbW90aW9uUHJvdmlkZXJNb2NrIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMuX2FwaSA9IG5ldyBGYWtlUHJvbUFwaSgpO1xyXG4gICAgfVxyXG4gICAgZ2V0KCkge1xyXG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uKiAoKSB7XHJcbiAgICAgICAgICAgIHJldHVybiB5aWVsZCB0aGlzLl9hcGkuZ2V0KCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICBnZXRCeUlkKGlkKSB7XHJcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24qICgpIHtcclxuICAgICAgICAgICAgcmV0dXJuIHlpZWxkIHRoaXMuX2FwaS5nZXRCeUlkKGlkKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIGdldFByb21vdGlvbnNCZXR3ZWVuKHN0YXJ0LCBlbmQpIHtcclxuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiogKCkge1xyXG4gICAgICAgICAgICBjb25zdCBzdGFydERhdGUgPSBuZXcgRGF0ZShzdGFydC55ZWFyLCBzdGFydC5tb250aCAtIDEsIHN0YXJ0LmRheSk7XHJcbiAgICAgICAgICAgIGNvbnN0IGVuZERhdGUgPSBuZXcgRGF0ZShlbmQueWVhciwgZW5kLm1vbnRoIC0gMSwgZW5kLmRheSk7XHJcbiAgICAgICAgICAgIHJldHVybiB5aWVsZCB0aGlzLl9hcGkuZ2V0QmV0d2VlbihzdGFydERhdGUsIGVuZERhdGUpO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgYWRkKHByb21vdGlvbikge1xyXG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uKiAoKSB7XHJcbiAgICAgICAgICAgIHlpZWxkIHRoaXMuX2FwaS5hZGQocHJvbW90aW9uKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIHVwZGF0ZShpZCwgcHJvbW90aW9uKSB7XHJcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24qICgpIHtcclxuICAgICAgICAgICAgeWllbGQgdGhpcy5fYXBpLnVwZGF0ZShpZCwgcHJvbW90aW9uKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLmRlZmF1bHQgPSBQcm9tb3Rpb25Qcm92aWRlck1vY2s7XHJcbjtcclxud2luZG93LlZ1ZVByb21vdGlvbnNQcm92aWRlciA9IG5ldyBQcm9tb3Rpb25Qcm92aWRlck1vY2soKTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBEb21haW5JdGVtIHtcclxuICAgIGNvbnN0cnVjdG9yKGRpc3BsYXlOYW1lLCBuYW1lLCBwZCkge1xyXG4gICAgICAgIHRoaXMuZGlzcGxheU5hbWUgPSBkaXNwbGF5TmFtZTtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMucGQgPSBwZDtcclxuICAgICAgICB0aGlzLnJhbmRvbUlkID0gbmFub2lkXzEubmFub2lkKCk7XHJcbiAgICB9XHJcbn1cclxuZXhwb3J0cy5kZWZhdWx0ID0gRG9tYWluSXRlbTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuZXhwb3J0cy5FeHRyYUZpZWxkID0gZXhwb3J0cy5BY3Rpdml0eSA9IHZvaWQgMDtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBBY3Rpdml0eSB7XHJcbiAgICBjb25zdHJ1Y3RvcihuYW1lLCBwcm9tb1Byb3ZpZGVyKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLnByb21vUHJvdmlkZXIgPSBwcm9tb1Byb3ZpZGVyO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkFjdGl2aXR5ID0gQWN0aXZpdHk7XHJcbmNsYXNzIEV4dHJhRmllbGQge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgcHJvbW9Qcm92aWRlciA9IC0xLCBkYXRhVHlwZSA9IG51bGwsIGRlc2NyaXB0aW9uID0gbnVsbCkge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy5wcm9tb1Byb3ZpZGVyID0gcHJvbW9Qcm92aWRlcjtcclxuICAgICAgICB0aGlzLmRhdGFUeXBlID0gZGF0YVR5cGU7XHJcbiAgICAgICAgdGhpcy5kZXNjcmlwdGlvbiA9IGRlc2NyaXB0aW9uO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkV4dHJhRmllbGQgPSBFeHRyYUZpZWxkO1xyXG5jbGFzcyBHYW1lUHJvdmlkZXIge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgZ2FtZXMgPSBbXSwgZG9tYWluLCB1cmwsIFxyXG4gICAgLy8gKyBGb3IgcHJvbW90aW9uc1xyXG4gICAgY2F0ZWdvcnkgPSBudWxsLCBhY3Rpdml0aWVzID0gW10sIGV4dHJhRmllbGRzID0gW10gLy8gLVxyXG4gICAgKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLmdhbWVzID0gZ2FtZXM7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XHJcbiAgICAgICAgdGhpcy5jYXRlZ29yeSA9IGNhdGVnb3J5O1xyXG4gICAgICAgIHRoaXMuYWN0aXZpdGllcyA9IGFjdGl2aXRpZXM7XHJcbiAgICAgICAgdGhpcy5leHRyYUZpZWxkcyA9IGV4dHJhRmllbGRzO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgICAgICB0aGlzLmFjdGl2ZSA9IGZhbHNlO1xyXG4gICAgfVxyXG4gICAgYWRkR2FtZShnYW1lKSB7XHJcbiAgICAgICAgdGhpcy5nYW1lcy5wdXNoKGdhbWUpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IEdhbWVQcm92aWRlcjtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuZXhwb3J0cy5Qcm9tb3Rpb24gPSBleHBvcnRzLlByb21vdGlvblRoZW1lID0gdm9pZCAwO1xyXG5jb25zdCBuYW5vaWRfMSA9IHJlcXVpcmUoXCJuYW5vaWRcIik7XHJcbmNvbnN0IHJydWxlXzEgPSByZXF1aXJlKFwicnJ1bGVcIik7XHJcbmNsYXNzIFNjaGVkdWxlIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgICAgICB0aGlzLnJydWxlU3RyaW5nID0gJyc7XHJcbiAgICAgICAgdGhpcy5sZW5ndGhJbkRheXMgPSAxO1xyXG4gICAgICAgIHRoaXMuZGF0ZVN0YXJ0ID0gbnVsbDtcclxuICAgICAgICB0aGlzLmRhdGVVbnRpbCA9IG51bGw7XHJcbiAgICAgICAgdGhpcy5zaW5nbGVEYXkgPSB0cnVlO1xyXG4gICAgfVxyXG4gICAgc2V0RGV0YWlsc0J5UnJ1bGUoKSB7XHJcbiAgICAgICAgaWYgKCF0aGlzLnJydWxlU3RyaW5nKSB7XHJcbiAgICAgICAgICAgIHJldHVybjtcclxuICAgICAgICB9XHJcbiAgICAgICAgY29uc3QgcnJ1bGUgPSBycnVsZV8xLlJSdWxlLmZyb21TdHJpbmcodGhpcy5ycnVsZVN0cmluZyk7XHJcbiAgICAgICAgdGhpcy5kYXRlU3RhcnQgPSBycnVsZS5vcHRpb25zLmR0c3RhcnQ7XHJcbiAgICAgICAgdGhpcy5kYXRlVW50aWwgPSBycnVsZS5vcHRpb25zLnVudGlsO1xyXG4gICAgICAgIHRoaXMuc2luZ2xlRGF5ID0gcnJ1bGUub3B0aW9ucy5jb3VudCA9PT0gbnVsbCB8fCBycnVsZS5vcHRpb25zLmNvdW50IDw9IDE7XHJcbiAgICAgICAgcmV0dXJuIHJydWxlO1xyXG4gICAgfVxyXG59XHJcbmNsYXNzIFByb21vdGlvblRoZW1lIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMuY29sb3JOYW1lID0gJ3ByaW1hcnknO1xyXG4gICAgICAgIHRoaXMuY29sb3JIZXggPSAnIzE5NzZkMic7XHJcbiAgICB9XHJcbiAgICBnZXQgY29sb3IoKSB7XHJcbiAgICAgICAgcmV0dXJuIHRoaXMuY29sb3JIZXggfHwgdGhpcy5jb2xvcjtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLlByb21vdGlvblRoZW1lID0gUHJvbW90aW9uVGhlbWU7XHJcbmNsYXNzIFByb21vdGlvbiB7XHJcbiAgICBjb25zdHJ1Y3RvcigpIHtcclxuICAgICAgICB0aGlzLmlkID0gbmFub2lkXzEubmFub2lkKCk7XHJcbiAgICAgICAgdGhpcy50aXRsZSA9ICcnO1xyXG4gICAgICAgIHRoaXMuZGVzY3JpcHRpb24gPSAnJztcclxuICAgICAgICB0aGlzLnNjaGVkdWxlID0gbmV3IFNjaGVkdWxlKCk7XHJcbiAgICAgICAgdGhpcy5yZXdhcmQgPSBudWxsO1xyXG4gICAgICAgIC8vIHByb3ZpZGVyOiBHYW1lUHJvdmlkZXIgfCBudWxsID0gbnVsbFxyXG4gICAgICAgIHRoaXMuY2F0ZWdvcnkgPSBudWxsO1xyXG4gICAgICAgIHRoaXMuY2hhbGxlbmdlR3JvdXBzID0gW107IC8vIEEgbGlzdCBvZiBncm91cHMgb2YgY2hhbGxlbmdlcywgZWl0aGVyIG5lZWRzIHRvIGJlIGNvbXBsZXRlZFxyXG4gICAgICAgIHRoaXMuZG9tYWluID0gbnVsbDtcclxuICAgICAgICB0aGlzLnRoZW1lID0gbmV3IFByb21vdGlvblRoZW1lKCk7XHJcbiAgICB9XHJcbn1cclxuZXhwb3J0cy5Qcm9tb3Rpb24gPSBQcm9tb3Rpb247XHJcbiJdLCJzb3VyY2VSb290IjoiIn0=