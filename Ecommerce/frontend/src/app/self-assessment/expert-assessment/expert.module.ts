import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListComponent } from './list/list.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from './../../layout/components/content-header/content-header.module';
import { RouterModule, Routes } from '@angular/router';
import { DetailComponent } from './detail/detail.component';


const routes: Routes = [
  {
    path: 'list',
    component: ListComponent ,
    data: {animation: 'feather'}
  }
]
@NgModule({
  declarations: [
     ListComponent,
     DetailComponent,
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
export class ExpertModule { }
