import type { Analysis } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import MarkdownRenderer from '@/components/ui/markdown-renderer'
import { Skeleton } from './ui/skeleton'
import { useGlobalState } from '@/hooks/use-global-state'
import { CircleMinus, ExternalLink, Refresh } from './icons/tabler'
import { toast } from 'sonner'
import { handleSearch } from '@/lib/handleSearch'
import { cn } from '@/lib/utils'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip'

function Summary({ summary }: { summary: string }) {
  return (
    <>
      <strong className="text-2xl text-left">Summary:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        {summary}
      </div>
    </>
  )
}

function RelatedDocs({
  related_docs,
}: {
  related_docs: Analysis['content'][number]['related_docs']
}) {
  return (
    <>
      <strong className="text-2xl text-left">Related Docs:</strong>
      <div className="w-full rounded-md border p-4 text-left">
        <ul className="list-disc pl-2">
          {related_docs.length > 0 ? (
            related_docs.map((doc) => {
              return (
                <li key={doc} className="hover:underline">
                  <a href={doc} target="_blank">
                    {doc}
                  </a>
                </li>
              )
            })
          ) : (
            <li>None</li>
          )}
        </ul>
      </div>
    </>
  )
}

function DetailedAnalysis({
  detailed_analysis,
}: {
  detailed_analysis: string
}) {
  return (
    <>
      <strong className="text-2xl text-left">Detailed Analysis:</strong>
      <div className="w-full rounded-md border p-4 text-left">
        <MarkdownRenderer>{detailed_analysis}</MarkdownRenderer>
      </div>
    </>
  )
}

function RefreshAnalysis({ analysis }: { analysis: Analysis }) {
  const addAnalysis = useGlobalState((state) => state.addAnalysis)

  return (
    <Tooltip>
      <TooltipTrigger>
        <div
          className="cursor-pointer text-blue-500 hover:text-blue-700"
          onClick={(e) => {
            e.stopPropagation()
            // eslint-disable-next-line @typescript-eslint/no-empty-function
            handleSearch(analysis.repository, () => {}, addAnalysis)
          }}
        >
          <Refresh />
        </div>
      </TooltipTrigger>
      <TooltipContent>
        <p>Rerun</p>
      </TooltipContent>
    </Tooltip>
  )
}

function DeleteAnalysis({
  login,
  analysis,
}: {
  login: string
  analysis: Analysis
}) {
  return (
    <AlertDialog>
      <AlertDialogTrigger
        className="cursor-pointer text-red-500 hover:text-red-700 h-[24px]"
        onClick={(e) => {
          e.stopPropagation()
        }}
      >
        <Tooltip>
          <TooltipTrigger>
            <CircleMinus />
          </TooltipTrigger>
          <TooltipContent>
            <p>Delete</p>
          </TooltipContent>
        </Tooltip>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Are you sure you want to delete the analysis?
          </AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel
            onClick={(e) => {
              e.stopPropagation()
            }}
          >
            Cancel
          </AlertDialogCancel>
          <AlertDialogAction>
            <div
              onClick={(e) => {
                e.stopPropagation()
                void deleteAnalysis(login, analysis.id).then(() => {
                  useGlobalState.setState((state) => ({
                    analyses: state.analyses.filter(
                      (a) => a.id !== analysis.id,
                    ),
                  }))
                })
              }}
            >
              Delete
            </div>
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}

async function deleteAnalysis(login: string, analysisId: string) {
  const res = await fetch(
    `${import.meta.env.VITE_USERS_URL}/users/${login}/analysis/${analysisId}`,
    {
      method: 'DELETE',
    },
  )
  if (!res.ok) {
    toast.error('Failed to remove analysis')
    throw new Error(`Failed to delete analysis: ${res.statusText}`)
  }
  toast.info('Analysis removed')
}

function OpenRepository({ repoUrl }: { repoUrl: string }) {
  return (
    <Tooltip>
      <TooltipTrigger
        className="cursor-pointer"
        onClick={() => {
          window.open(repoUrl, '_blank', 'noopener,noreferrer')
        }}
      >
        <ExternalLink />
      </TooltipTrigger>
      <TooltipContent>
        <p>Open Repository</p>
      </TooltipContent>
    </Tooltip>
  )
}

function HighlightedPing() {
  return (
    <span className="absolute top-0 right-0 -mt-1 -mr-1 flex size-3">
      <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-sky-400 opacity-75"></span>{' '}
      <span className="relative inline-flex size-3 rounded-full bg-sky-500"></span>
    </span>
  )
}

function Analysis(analysis: Analysis) {
  const markAnalysisNotHighlighted = useGlobalState(
    (state) => state.markAnalysisNotHighlighted,
  )
  const createdAt = analysis.created_at.toLocaleString('de-DE', {
    timeZone: 'Europe/Berlin',
  })
  const login = useGlobalState((state) => state.login)
  return (
    <AccordionItem value={analysis.id}>
      <AccordionTrigger
        className={cn(
          'text-center border m-1 p-2 relative',
          analysis.highlighted ? 'border-primary animate-pulse' : '',
        )}
        onClick={() => {
          markAnalysisNotHighlighted(analysis.id)
        }}
      >
        {analysis.highlighted && <HighlightedPing />}
        <div className="flex flex-1 justify-between items-center">
          <div>
            <strong>Repository: </strong>
            {analysis.repository}
          </div>
          <div className="flex gap-2 items-center">
            <span>{createdAt}</span>
            <OpenRepository repoUrl={analysis.repository} />
            <RefreshAnalysis analysis={analysis} />
            {analysis.id !== 'unknown' && login && (
              <DeleteAnalysis login={login} analysis={analysis} />
            )}
          </div>
        </div>
      </AccordionTrigger>
      <AccordionContent className="flex flex-col gap-4 text-balance">
        <Accordion type="single" collapsible>
          {analysis.content.map((content) => (
            <AccordionItem value={content.filename} key={content.filename}>
              <AccordionTrigger className="mx-8 my-1 px-2 border">
                {content.filename}
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4 mx-8 mb-5 mt-4">
                <Summary summary={content.summary} />
                <DetailedAnalysis
                  detailed_analysis={content.detailed_analysis}
                />
                <RelatedDocs related_docs={content.related_docs} />
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </AccordionContent>
    </AccordionItem>
  )
}

export default function Analyses() {
  const analyses = useGlobalState((state) => state.analyses)
  const loading = useGlobalState((state) => state.loading)
  return (
    <div className="mt-8">
      <h2 className="text-4xl m-2">Analyses:</h2>
      <Accordion type="single" collapsible className="w-full">
        {loading ? (
          <Skeleton className="h-20 w-full" />
        ) : analyses.length === 0 ? (
          <div className="text-center text-gray-500">
            No analyses available yet.
          </div>
        ) : (
          analyses.map((analysis) => (
            <Analysis
              key={analysis.id + analysis.created_at.toISOString()}
              {...analysis}
            />
          ))
        )}
      </Accordion>
    </div>
  )
}
