import { ListLogComponent } from './list-log/list-log.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { DetailLogComponent } from './detail-log/detail-log.component';


const routes: Routes = [
  {
    path: 'list-log',
    component: ListLogComponent,
    data: { animation: 'feather' }
  }
]

@NgModule({
  declarations: [
    ListLogComponent,
    DetailLogComponent,
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
    TranslateModule
  ]
})
export class LogModule { }
