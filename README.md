# SevenBridges

Example usage:

    cgccli --token 9e974e8e15cc4b62aee5d5df26724d20 projects list
    cgccli --token 9e974e8e15cc4b62aee5d5df26724d20 files list --project igor_7495031/copy-of-smart-variant-filtering
    cgccli --token 9e974e8e15cc4b62aee5d5df26724d20 files stat --file 5ccc61fae4b04e1403510b8d
    cgccli --token 9e974e8e15cc4b62aee5d5df26724d20 files update --file 5ccc61fae4b04e1403510b8d name=ERR17432.151x.vcf metadata.sample_id=sam_id_123 tag=blue tag=yellow
    cgccli --token 9e974e8e15cc4b62aee5d5df26724d20 files download --file 5ccc61fae4b04e1403510b8d --dest /home/igor/Desktop/task/txt
     

Commands parameters:

cgccli --token {token} files list   --project={string}
                                    --parent={folder_id}
                                    name={name}
                                    fields=f1,f2,f3...
                                    tag={String}
                                    limit={int}
                                    metadata.{field}={String}
                                    origin.task={String}
                                    origin.dataset={String}
            
cgccli --token {token} files stat   --file {file_id}
                                    fields=f1,f2,f3..

cgccli --token {token} files update   --file {file_id}
                                      name={string}
                                      metadata.{key}={value}
                                      tag={String}
                                      fields=f1,f2,f3..

cgccli --token {token} files download  --file {file_id}
                                       fields=f1,f2,f3...





