import { FormsModule } from '@angular/forms';
import { CardSnippetModule } from './../../../@core/components/card-snippet/card-snippet.module';
import { CoreTouchspinModule } from './../../../@core/components/core-touchspin/core-touchspin.module';
import { NgxMaskModule } from 'ngx-mask';
import { CoreCardModule } from './../../../@core/components/core-card/core-card.module';
import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CoreDirectivesModule } from './../../../@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CorePipesModule } from './../../../@core/pipes/pipes.module';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from './../../layout/components/content-header/content-header.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatisticalComponent } from './statistical/statistical.component';
import { RouterModule, Routes } from '@angular/router';
import { AddStatisticalComponent } from './add-statistical/add-statistical.component';
import { DetailStatisticalComponent } from './detail-statistical/detail-statistical.component';
import { ExportStatisticalComponent } from './export-statistical/export-statistical.component';
import { CopyStatisticalComponent } from './copy-statistical/copy-statistical.component';

const routes: Routes = [
  {
    path: "list",
    component: StatisticalComponent,
    data: {animation: 'feather'}
  }
]

@NgModule({
  declarations: [
    StatisticalComponent,
    AddStatisticalComponent,
    DetailStatisticalComponent,
    ExportStatisticalComponent,
    CopyStatisticalComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    CorePipesModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    TranslateModule,
    CoreCardModule,
    NgxMaskModule.forRoot(),
    CoreTouchspinModule,
    CardSnippetModule,
  ]
})
export class StatisticalModule { }
