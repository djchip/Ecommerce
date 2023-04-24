import { NgxMaskModule } from 'ngx-mask';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListFormComponent } from './list-form/list-form.component';
import { Routes, RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from '@core/directives/directives';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddFormComponent } from './add-form/add-form.component';
import { EditFormComponent } from './edit-form/edit-form.component';
import { DetailFormComponent } from './detail-form/detail-form.component';
import { TranslateModule } from '@ngx-translate/core';
import { CopyFormComponent } from './copy-form/copy-form.component';
import { TreeModule } from '@circlon/angular-tree-component';
import { InputCodeComponent } from './input-code/input-code.component';
import { CopyMultiFormComponent } from './copy-multi-form/copy-multi-form.component';



const routes: Routes = [
  {
    path: 'list',
    component: ListFormComponent,
    data: { animation: 'feather' }
  }

];

@NgModule({
  declarations: [
    ListFormComponent,
    AddFormComponent,
    EditFormComponent,
    DetailFormComponent,
    CopyFormComponent,
    InputCodeComponent,
    CopyMultiFormComponent,
  ],
  imports: [
    NgSelectModule,
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule,
    TreeModule,
    NgxMaskModule.forRoot(),
  ],
  exports:[
    DetailFormComponent,
  ]
})
export class FormModule { }
