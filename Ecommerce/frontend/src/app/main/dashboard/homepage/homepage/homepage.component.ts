import { Component, OnInit, ViewChild, ViewEncapsulation, ChangeDetectorRef, NgZone } from '@angular/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexStroke,
  ApexDataLabels,
  ApexGrid,
  ApexTitleSubtitle,
  ApexTooltip,
  ApexPlotOptions,
  ApexYAxis,
  ApexFill,
  ApexMarkers,
  ApexTheme,
  ApexNonAxisChartSeries,
  ApexLegend,
  ApexResponsive,
  ApexStates,
  ChartComponent
} from 'ng-apexcharts';

import { colors } from 'app/colors.const';
import { CoreConfigService } from '@core/services/config.service';
import { ProgramService } from 'app/exhibition/programs-management/programs.service';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { elementAt } from 'rxjs/operators';
import { DashboardService } from '../../dashboard.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CardAnalyticsService } from 'app/main/ui/card/card-analytics/card-analytics.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { pipeline } from 'stream';
import { FormService } from 'app/academy-database/form-management/form.service';

type ApexXAxis = {
  type?: "category" | "datetime" | "numeric";
  categories?: any;
  labels?: {
    style?: {
      colors?: string | string[];
      fontSize?: string;
    };
  };
};


export interface ChartOptions {
  series?: ApexAxisChartSeries;
  chart?: ApexChart;
  xaxis?: ApexXAxis;
  dataLabels?: ApexDataLabels;
  grid?: ApexGrid;
  stroke?: ApexStroke;
  legend?: ApexLegend;
  title?: ApexTitleSubtitle;
  colors?: string[];
  tooltip?: ApexTooltip;
  plotOptions?: ApexPlotOptions;
  yaxis?: ApexYAxis;
  fill?: ApexFill;
  labels?: string[];
  markers: ApexMarkers[];
  theme: ApexTheme;
  responsive: ApexResponsive[];
  states: ApexStates;
}

export interface ChartOptions2 {
  // Apex-non-axis-chart-series
  series?: ApexNonAxisChartSeries;
  chart?: ApexChart;
  stroke?: ApexStroke;
  tooltip?: ApexTooltip;
  dataLabels?: ApexDataLabels;
  fill?: ApexFill;
  colors?: string[];
  legend?: ApexLegend;
  labels?: any;
  plotOptions?: ApexPlotOptions;
  responsive?: ApexResponsive[];
  markers?: ApexMarkers;
  xaxis?: ApexXAxis;
  yaxis?: ApexYAxis;
  states?: ApexStates;
  grid: ApexGrid;
}

@Component({
  selector: 'app-list-user',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HomepageComponent implements OnInit {
  @ViewChild('apexColumnChartRef') apexColumnChartRef: any;
  @ViewChild('customerChartoptionsRef') customerChartoptionsRef: any;
  @ViewChild("chart") chart: ChartComponent;
  public chartOptions: Partial<ChartOptions>;


  // Public
  public contentHeader: object;

  // Charts Of Interface Chartoptions
  public sessionChartoptions: Partial<ChartOptions>;
  public orderChartoptions: Partial<ChartOptions>;
  public customerChartoptions: Partial<ChartOptions>;
  public supportChartoptions: Partial<ChartOptions>;
  public goalChartoptions: Partial<any>;
  public revenueReportChartoptions: Partial<ChartOptions2>;

  // Charts Of Interface Chartoptions2
  public budgetChartoptions: Partial<ChartOptions2>;
  public salesChartoptions: Partial<ChartOptions2>;
  public revenueChartoptions: Partial<ChartOptions2>;
  public avgsessionChartoptions: Partial<ChartOptions2>;
  public salesavgChartoptions: Partial<ChartOptions2>;
  public retentionChartoptions: Partial<ChartOptions2>;
  public earningChartoptions;
  public isMenuToggled = false;


  public apexLineChart: Partial<ChartOptions>;
  public apexLineAreaChart: Partial<ChartOptions>;
  public apexColumnChart: Partial<ChartOptions>;
  public apexBarChart: Partial<ChartOptions>;
  public apexCandlestickChart: Partial<ChartOptions>;
  public apexScatterChart: Partial<ChartOptions>;
  public apexHeatmapChart: Partial<ChartOptions>;
  public apexDonutChart: Partial<ChartOptions2>;
  public apexRadialChart: Partial<ChartOptions2>;
  public apexRadarChart: Partial<ChartOptions>;

  public listPrograms = [];
  public listProgramsName = [];
  public listProgramsNameEn = [];
  public listProgramsHasFile = [];
  public listProgramsQuantityHasFile = [];
  public listProgramsHasNotFile = [];
  public listProgramsQuantityHasNotFile = [];
  public listYears = [];
  public arrayYears = [];
  public listProgramQuantity = [];
  public programQuantity = [];
  public thisYear;
  public thisYearDb;
  public quantityFormUploaded: number;
  public quantityFormNotUploaded: number;

  public radioModel = 1;

  public series = [];
  public uploaded;
  public notUploaded;
  public totalDb;
  public currentLang = this._translateService.currentLang;



  // ng2-flatpickr options
  public DateRangeOptions = {
    altInput: true,
    altFormat: 'Y',
    ariaDateFormat: 'Y'
  };

  // Color Variables
  chartColors = {
    column: {
      series1: '#17a2b8',
      series2: '#ff9f43',
      bg: '#00cfe800'
    },
    success: {
      shade_100: '#7eefc7',
      shade_200: '#06774f'
    },
    donut: {
      series1: '#ffe700',
      series2: '#00d4bd',
      series3: '#826bf8',
      series4: '#2b9bf4',
      series5: '#FFA1A1'
    },
    area: {
      series3: '#a4f8cd',
      series2: '#60f2ca',
      series1: '#2bdac7'
    }
  };

  // Heatmap data generate
  public generateHeatmapData(count, yrange) {
    var i = 0;
    var series = [];
    while (i < count) {
      var x = 'w' + (i + 1).toString();
      var y = Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min;

      series.push({
        x: x,
        y: y
      });
      i++;
    }
    return series;
  }

  constructor(
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    private _coreConfigService: CoreConfigService,
    private _changeLanguageService: ChangeLanguageService,
    private service: DashboardService,
    private formService: FormService) {

    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      this.loadChart();
      this.loadChartDb();
    });

    this.chartOptions = {
      series: [
        {
          name: "Chương trình",
          data: this.programQuantity
        }
      ],
      chart: {
        height: 403.93,
        type: "bar"
      },
      colors: [
        "#008FFB"
      ],
      plotOptions: {
        bar: {
          columnWidth: "45%",
          distributed: true
        }
      },
      dataLabels: {
        enabled: true,
        style: {
          fontSize: "16px"
        }
      },
      legend: {
        show: false
      },
      grid: {
        show: false
      },
      xaxis: {
        categories: this.arrayYears,
        labels: {
          style: {
            colors: [
              "#008FFB"
            ],
            fontSize: "12px"
          }
        }
      }
    };
  }

  loadChartDb() {
    this.customerChartoptions = {
      chart: {
        type: 'pie',
        height: 345,
        toolbar: {
          show: false
        }
      },
      labels: [this.currentLang == 'en' ? ' Uploaded' : ' Đã tải cơ sở dữ liệu', this.currentLang == 'en' ? ' Not uploaded' : ' Chưa tải cơ sở dữ liệu'],
      dataLabels: {
        enabled: false
      },
      legend: { show: false },
      stroke: {
        width: 4
      },
      colors: [colors.solid.primary, colors.solid.warning, colors.solid.danger]
    };
  }

  loadChart() {
    let changeLanguage = this.currentLang == 'en' ? ' exhibition' : ' minh chứng';
    this.apexColumnChart = {
      series: [
        {
          name: this._translateService.instant('ACTION.HASFILE'),
          data: this.listProgramsHasFile
        },
        {
          name: this._translateService.instant('ACTION.NOTFILE'),
          data: this.listProgramsHasNotFile
        }
      ],
      chart: {
        type: 'bar',
        height: 400,
        stacked: true,
        toolbar: {
          show: false
        }
      },
      grid: {
        xaxis: {
          lines: {
            show: true
          }
        }
      },
      legend: {
        show: true,
        position: 'top',
        horizontalAlign: 'left'
      },
      plotOptions: {
        bar: {
          columnWidth: '15%',
          colors: {
            backgroundBarColors: [
              this.chartColors.column.bg,
              this.chartColors.column.bg,
              this.chartColors.column.bg,
              this.chartColors.column.bg,
              this.chartColors.column.bg
            ],
            backgroundBarRadius: 10
          }
        }
      },
      colors: [this.chartColors.column.series1, this.chartColors.column.series2],
      dataLabels: {
        enabled: false
      },
      stroke: {
        show: true,
        width: 2,
        colors: ['transparent']
      },
      xaxis: {
        categories: this.currentLang == 'en' ? this.listProgramsNameEn : this.listProgramsName
      },
      yaxis: {
        title: {
          text: ' ' + changeLanguage
        }
      },
      fill: {
        opacity: 1
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val + ' ' + changeLanguage;
          }
        }
      }
    };
  }

  getListYearInDataBase() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListYearInDataBase(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listYears = data.content;
          this.listYears.forEach(ele => {
            this.arrayYears.push(ele)
          })

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getProgramQuantityByYear() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getProgramQuantityByYear(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listProgramQuantity = data.content;
          this.listProgramQuantity.forEach(ele => {
            this.programQuantity.push(ele)
          })

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getYearNow() {
    let params = {
      method: "GET",
    };
    this.service
      .getYearNow(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.thisYear = data.content;
          this.thisYearDb = data.content;
          this.getListPrograms(this.thisYear);
          this.loadData();
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getListPrograms(year) {
    let params = {
      method: "GET",
      byYear: year
    };
    Swal.showLoading();
    this.service
      .getListPrograms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listPrograms = data.content;
          this.listProgramsName = this.listPrograms.map((ele) => ele.name);
          this.listProgramsNameEn = this.listPrograms.map((ele) => ele.nameEn);

          console.log(" DAY " + this.listProgramsName);

          // this.listPrograms.forEach(element => {
          //   this.listProgramsName.push(element.name)
          //   this.listProgramsNameEn.push(element.nameEn)
          // })

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getListQuantityProofHasFile(year) {
    let params = {
      method: "GET",
      byYear: year
    };
    Swal.showLoading();
    this.service
      .getListQuantityProofHasFile(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listProgramsHasFile = data.content;
          // this.listProgramsHasFile.forEach(ele => {
          //   this.listProgramsQuantityHasFile.push(ele)
          // })
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getListQuantityProofHasNotFile(year) {
    let params = {
      method: "GET",
      byYear: year
    };
    Swal.showLoading();
    this.service
      .getListQuantityProofHasNotFile(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listProgramsHasNotFile = data.content;
          this.listProgramsHasNotFile.forEach(ele => {
            this.listProgramsQuantityHasNotFile.push(ele)
          })

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getListFormNotUploaded(year) {
    let params = {
      method: "GET",
      byYear: year
    };
    Swal.showLoading();
    this.formService
      .getListFormNotUploaded(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.quantityFormNotUploaded = data.content;
          this.series[1] = this.quantityFormNotUploaded;
          this.notUploaded = this.quantityFormNotUploaded;

          let params2 = {
            method: "GET",
            byYear: year
          };

          this.formService
            .getTotalForm(params2)
            .then((data) => {
              Swal.close();
              let response = data;
              if (response.code === 0) {
                this.totalDb = data.content;
                this.quantityFormUploaded = this.totalDb - this.quantityFormNotUploaded;
                // this.series.push(this.quantityFormUploaded)
                this.series[0] = this.quantityFormUploaded;
                this.uploaded = this.quantityFormUploaded;
              } else {
                Swal.fire({
                  icon: "error",
                  title: response.errorMessages,
                });
              }
            })
            .catch((error) => {
              Swal.close();
              Swal.fire({
                icon: "error",
                title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
                confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
              });
            });
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getTotalFormByYear(year) {
    let params = {
      method: "GET",
      byYear: year
    };
    Swal.showLoading();
    this.formService
      .getTotalFormByYear(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.totalDb = data.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }


  changeYear(newYear: any) {
    this.thisYear = newYear;
    this.listProgramsQuantityHasFile = []
    this.listProgramsQuantityHasNotFile = []
    this.listProgramsName = []
    this.listProgramsNameEn = []
    this.getListQuantityProofHasFile(this.thisYear);
    this.getListQuantityProofHasNotFile(this.thisYear);
    this.getListPrograms(this.thisYear);
    this.loadChangeYear();
  }

  changeYearDatabase(newYear: any) {
    this.thisYearDb = newYear;
    this.series = [];
    this.quantityFormNotUploaded = 0;
    this.quantityFormUploaded = 0;
    // this.getTotalForm(this.thisYearDb)
    this.getListFormNotUploaded(this.thisYearDb)
    this.getTotalFormByYear(this.thisYearDb)
    this.loadChartDb();
    this.loadChangeYearDb();
  }

  loadData() {
    this.getListPrograms(this.thisYear);
    this.getListQuantityProofHasFile(this.thisYear);
    this.getListQuantityProofHasNotFile(this.thisYear);
    this.getListFormNotUploaded(this.thisYearDb);
    this.getTotalFormByYear(this.thisYearDb)
  }


  ngOnInit(): void {
    this.getYearNow();
    this._coreTranslationService.translate(eng, vie);
    this.contentHeader = {
      headerTitle: 'Apex Charts',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Home',
            isLink: true,
            link: '/'
          },
          {
            name: 'Table',
            isLink: true,
            link: '/'
          },
          {
            name: 'Apex Charts',
            isLink: false
          }
        ]
      }
    };
    this.getListYearInDataBase();
    this.loadChart();
    this.loadChartDb();
    this.getProgramQuantityByYear()

  }

  loadChangeYear() {
    setTimeout(() => {
      this.getListPrograms(this.thisYear);
      this.getListQuantityProofHasFile(this.thisYear);
      this.getListQuantityProofHasNotFile(this.thisYear);
      this.loadChart();
    }, 800)

    
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.loadData();
      this.loadChart();
      this.loadChartDb();
    }, 800)

    
  }

  loadChangeYearDb() {
    setTimeout(() => {
      this.loadChartDb();
    }, 800)
  }
}

